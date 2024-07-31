package nl.cloudplusplus.evaluation.models

data class Record (
    var id: Int,
    var defaultFormId: Int,
    var form: String,
    var title: String,
    var sections: Array<Section>,
    var fields: Array<Field>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (id != other.id) return false
        if (defaultFormId != other.defaultFormId) return false
        if (form != other.form) return false
        if (title != other.title) return false
        if (!sections.contentEquals(other.sections)) return false
        if (!fields.contentEquals(other.fields)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + defaultFormId
        result = 31 * result + form.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + sections.contentHashCode()
        result = 31 * result + fields.contentHashCode()
        return result
    }
}