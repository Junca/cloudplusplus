package nl.cloudplusplus.evaluation.models

import kotlinx.serialization.Serializable

@Serializable
data class Record (
    var id: Int,
    var defaultFormId: Int,
    var form: String,
    var title: String,
    var sections: Array<Section>? = arrayOf(),
    var fields: Array<Field>? = arrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (id != other.id) return false
        if (defaultFormId != other.defaultFormId) return false
        if (form != other.form) return false
        if (title != other.title) return false
        if (sections != null) {
            if (other.sections == null) return false
            if (!sections.contentEquals(other.sections)) return false
        } else if (other.sections != null) return false
        if (fields != null) {
            if (other.fields == null) return false
            if (!fields.contentEquals(other.fields)) return false
        } else if (other.fields != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + defaultFormId
        result = 31 * result + form.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (sections?.contentHashCode() ?: 0)
        result = 31 * result + (fields?.contentHashCode() ?: 0)
        return result
    }
}