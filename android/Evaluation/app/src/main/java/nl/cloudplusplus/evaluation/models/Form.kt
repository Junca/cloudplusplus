package nl.cloudplusplus.evaluation.models

import kotlinx.serialization.Serializable

@Serializable
data class Form (
    var id: Int? = null,
    var title: String,
    var sections: Array<Section>? = null,
    var fields: Array<Field>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Form

        if (id != other.id) return false
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
        var result = id ?: 0
        result = 31 * result + title.hashCode()
        result = 31 * result + (sections?.contentHashCode() ?: 0)
        result = 31 * result + (fields?.contentHashCode() ?: 0)
        return result
    }
}