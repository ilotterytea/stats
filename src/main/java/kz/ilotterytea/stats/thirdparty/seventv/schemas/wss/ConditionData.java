package kz.ilotterytea.stats.thirdparty.seventv.schemas.wss;

import com.google.gson.annotations.SerializedName;

/**
 * @author ilotterytea
 * @since 1.0
 */
public class ConditionData {
    @SerializedName("object_id")
    private String objectId;

    public ConditionData(String objectId) {
        this.objectId = objectId;
    }

    public ConditionData() {}

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
