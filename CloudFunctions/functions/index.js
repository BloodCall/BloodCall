const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();
exports.checkflag = functions.database.ref("/flagparent")
    .onUpdate((snapshot, context) => {
      const temptoken = "yourapptoken";
      const flag = snapshot.after.val();
      const statusMessage = `Message from the clouds as ${flag}`;
      const message = {
        notification: {
          title: "cfunction",
          body: statusMessage,
        },
        token: temptoken,
      };
      admin.messaging().send(message).then((response) => {
        console.log("Message sent successfully:", response);
        return response;
      })
          .catch((error) => {
            console.log("Error sending message: ", error);
          });
    });
