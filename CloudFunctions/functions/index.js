const functions = require("firebase-functions");

const admin = require("firebase-admin");
let hospital;
let topic;
admin.initializeApp();
exports.alertAdded = functions.database.ref("/Alerts/{alert_id}")
    .onCreate((snapshot, context) => {
      const alertData=snapshot.val();
      admin.database().ref("/Hospitals/"+alertData.owner)
          .once("value", (snap) => {
            console.log(snap.val());
            hospital=snap.val();
            topic=alertData.bloodType.replace("+", "pos").replace("-", "neg");
            admin.messaging().sendToTopic(
                "/topics/"+topic,
                {
                  data: {
                    alert: JSON.stringify(alertData),
                    hospital: JSON.stringify(hospital),
                  },
                }
            );
          }, (errorObject) => {
            console.log("The read failed: " + errorObject.name);
          });
    });
