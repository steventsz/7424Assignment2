package cs.unitec.steve.a7424;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class PlayerManage {

    private final String tag = "players";
    private final String collectionName = "players";
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    public void savePlayer(String uid, Runnable callback) {
        HashMap<String, Object> player = new HashMap<>();
        player.put("player", uid);
        fireStore.collection(collectionName)
                .add(player)
                .addOnSuccessListener(documentReference -> callback.run())
                .addOnFailureListener(e -> Log.d(tag, e.toString()));
    }

    public void findPlayer(String uid, Runnable onSuccess, Runnable onFailed) {
        fireStore.collection(collectionName)
                .whereEqualTo("player", uid).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.getDocuments().size() > 0) {
                        onSuccess.run();
                    } else {
                        onFailed.run();
                    }
                })
                .addOnFailureListener(e -> Log.d(tag, e.toString()));
    }
}
