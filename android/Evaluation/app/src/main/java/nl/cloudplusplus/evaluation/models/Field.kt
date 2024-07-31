package nl.cloudplusplus.evaluation.models

import kotlinx.serialization.Serializable

@Serializable
data class Field(
    var id: Int? = null,
    var type: String,
    var label: String,
    var name: String,
    var required: Boolean? = null,
    var uuid: String,
    var options: Array<Option>? = null,

    var fieldId: Int? = null,
    var value: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Field

        if (id != other.id) return false
        if (type != other.type) return false
        if (label != other.label) return false
        if (name != other.name) return false
        if (required != other.required) return false
        if (uuid != other.uuid) return false
        if (options != null) {
            if (other.options == null) return false
            if (!options.contentEquals(other.options)) return false
        } else if (other.options != null) return false
        if (fieldId != other.fieldId) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (required?.hashCode() ?: 0)
        result = 31 * result + uuid.hashCode()
        result = 31 * result + (options?.contentHashCode() ?: 0)
        result = 31 * result + (fieldId ?: 0)
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}