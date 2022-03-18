const functions = require('firebase-functions');

const admin = require('firebase-admin');
let hospital;
let topic;
let messageCondition;
admin.initializeApp();

exports.leaderboard = functions.https.onRequest((request, response) => {
  const users = [];
  admin.database().ref('/Users/')
      .once('value')
      .then((results) => {
        results.forEach((snapshot) => {
          users.push(snapshot.val());
        });
        users.sort((a, b) => (a.xp < b.xp) ? 1 : -1);
        response.send(users);
      });
});

exports.alertAdded = functions.database.ref('/Alerts/{alert_id}')
    .onCreate((snapshot, context) => {
      const alertData=snapshot.val();
      admin.database().ref('/Hospitals/'+alertData.owner)
          .once('value', (snap) => {
            console.log(snap.val());
            hospital=snap.val();
            topic=alertData.bloodType.replace('+', 'pos').replace('-', 'neg');
            console.log(topic);
            switch (topic) {
              case 'Apos':
                messageCondition='\'Apos\' in topics || \'Aneg\' in topics || \'Oneg\' in topics || \'Opos\' in topics';
                break;
              case 'Opos':
                messageCondition='\'Oneg\' in topics || \'Opos\' in topics';
                break;
              case 'Bpos':
                messageCondition='\'Bpos\' in topics || \'Bneg\' in topics || \'Oneg\' in topics || \'Opos\' in topics';
                break;
              case 'ABpos':
                // due to restriction of 5 topics we send to the top 5 most common.(according to redcrossblood.org).
                messageCondition='\'Apos\' in topics || \'ABpos\' in topics || \'Opos\' in topics || \'Bpos\' in topics || \'Oneg\' in topics';
                break;
              case 'Aneg':
                messageCondition='\'Aneg\' in topics || \'Oneg\' in topics';
                break;
              case 'Oneg':
                messageCondition='\'Oneg\' in topics';
                break;
              case 'Bneg':
                messageCondition='\'Bneg\' in topics || \'Oneg\' in topics';
                break;
              case 'ABneg':
                messageCondition='\'ABneg\' in topics || \'Aneg\' in topics || \'Oneg\' in topics || \'Bneg\' in topics';
                break;
            }
            console.log(messageCondition + ' message condition');
            const message = {
              data: {
                alert: JSON.stringify(alertData),
                hospital: JSON.stringify(hospital),
              },
              condition: messageCondition,
            };
            console.log(message);
            admin.messaging().send(message)
                .then((response)=>{
                  console.log('Successfully sent message:', response);
                }).catch((error) => {
                  console.log('Error sending message:', error);
                });
          }, (errorObject) => {
            console.log('The read failed: ' + errorObject.name);
          });
    });
exports.scheduledEventDateCheck = functions.pubsub.schedule('0 12 * * *')
    .timeZone('Greece/Athens')
    .onRun((context) => {
      console.log('Checking for expired events at 12:00 AM Greek time');
      const ref = admin.database.ref('/Events');
      ref.orderByChild('date').on('check_date', (snap) => {
        console.log('Child date:' + snap.val());
        console.log('Server timestamp:'+ admin.database.ServerValue.TIMESTAMP);
        const serverTime = admin.database.ServerValue.TIMESTAMP;
        // checking something cause of github
        console.log(Date.now());
        const date = new Date(serverTime);
        console.log(date);
        const eventDay = snap.val().trimEnd(6);
        const eventMonth = snap.val().trim(3);
        const eventYear = snap.val().trimStart(6);
        console.log('Trimmed event day is : '+eventDay);
        console.log('Trimmed event month is : '+eventMonth);
        console.log('Trimmed event year is : '+eventYear);
        if (parseInt(date.getFullYear().toString().trimEnd(2)) > eventYear) {
          ref.removeChild(snap.key);
        } else if (date.getMonth()+1 > eventMonth && parseInt(date.getFullYear().toString().trimEnd(2)) >= eventYear) {
          ref.removeChild(snap.key);
        } else if (date.getDay() > eventDay && date.getMonth()+1 >= eventMonth && parseInt(date.getFullYear().toString().trimEnd(2)) >= eventYear) {
          ref.removeChild(snap.key);
        } else {
          console.log('This good!');
        }
      });


      return null;
    });

