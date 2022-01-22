const functions = require("firebase-functions");

const admin = require("firebase-admin");

admin.initializeApp();
exports.alertAdded = functions.database.ref("/Alerts/{alert_id}")
    .onCreate((snapshot, context) => {
      const alertData = snapshot.val();
      admin.messaging().sendToTopic(
          "Alerts",
          {
            notification: {
              title: "Urgent Blood Call!",
              body: "Your blood is needed right now!",
            },
            data: {
              bloodType: alertData.bloodType,
              dateCreated: alertData.dateCreated,
              owner: alertData.owner,
            },
          }
      );
    });
