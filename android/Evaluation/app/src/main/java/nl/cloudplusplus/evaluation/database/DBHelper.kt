package nl.cloudplusplus.evaluation.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import nl.cloudplusplus.evaluation.models.Field
import nl.cloudplusplus.evaluation.models.Form
import nl.cloudplusplus.evaluation.models.Option
import nl.cloudplusplus.evaluation.models.Record
import nl.cloudplusplus.evaluation.models.Section

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        var query =
            ("CREATE TABLE $TABLE_DEFAULT_FORM ($ID_DEFAULT_FORM INTEGER PRIMARY KEY, $TITLE_DEFAULT_FORM TEXT)")
        db.execSQL(query)

        query =
            ("CREATE TABLE $TABLE_DEFAULT_SECTION ($ID_DEFAULT_SECTION INTEGER PRIMARY KEY, $ID_FORM_DEFAULT_DEFAULT_SECTION INTEGER,$TITLE_DEFAULT_SECTION TEXT,$FROM_DEFAULT_SECTION INTEGER,$TO_DEFAULT_SECTION INTEGER,$INDEX_DEFAULT_SECTION INTEGER,$UUID_DEFAULT_SECTION TEXT)")
        db.execSQL(query)

        query =
            ("CREATE TABLE $TABLE_DEFAULT_FIELD ($ID_DEFAULT_FIELD INTEGER PRIMARY KEY, $ID_FORM_DEFAULT_DEFAULT_FIELD INTEGER,$TYPE_DEFAULT_FIELD TEXT,$LABEL_DEFAULT_FIELD TEXT,$NAME_DEFAULT_FIELD TEXT,$REQUIRED_DEFAULT_FIELD INTEGER,$UUID_DEFAULT_FIELD TEXT)")
        db.execSQL(query)

        query =
            ("CREATE TABLE $TABLE_DEFAULT_OPTION_FIELD ($ID_DEFAULT_OPTION_FIELD INTEGER PRIMARY KEY, $ID_FIELD_DEFAULT_DEFAULT_OPTION_FIELD INTEGER,$LABEL_DEFAULT_OPTION_FIELD TEXT,$VALUE_DEFAULT_OPTION_FIELD TEXT)")
        db.execSQL(query)

        query =
            ("CREATE TABLE $TABLE_FORM ($ID_FORM INTEGER PRIMARY KEY, $ID_FORM_DEFAULT_FORM INTEGER,$TITLE_FORM TEXT)")
        db.execSQL(query)

        query =
            ("CREATE TABLE $TABLE_FIELD ($ID_FIELD INTEGER PRIMARY KEY, $ID_FORM_FIELD INTEGER,$ID_FIELD_FIELD INTEGER,$VALUE_FIELD TEXT)")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DEFAULT_FORM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DEFAULT_SECTION")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DEFAULT_FIELD")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DEFAULT_OPTION_FIELD")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FORM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FIELD")
        onCreate(db)
    }

    fun needImportJSON(): Boolean {
        val db = this.readableDatabase
        var hasRow: Boolean = false
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_DEFAULT_FORM", null)
        if (cursor!!.moveToFirst()) {
            hasRow = cursor.getInt(0) == 0
        }
        cursor.close()

        return hasRow
    }

    fun addDefaultForm(title: String): Long {

        val values = ContentValues()
        values.put(TITLE_DEFAULT_FORM, title)

        val db = this.writableDatabase
        val id = db.insert(TABLE_DEFAULT_FORM, null, values)
        db.close()

        return id
    }

    fun addDefaultSections(id: Long, sections: Array<Section>) {

        val db = this.writableDatabase
        sections.forEach { section ->

            val values = ContentValues()
            values.put(ID_FORM_DEFAULT_DEFAULT_SECTION, id)
            values.put(TITLE_DEFAULT_SECTION, section.title)
            values.put(FROM_DEFAULT_SECTION, section.from)
            values.put(TO_DEFAULT_SECTION, section.to)
            values.put(INDEX_DEFAULT_SECTION, section.index)
            values.put(UUID_DEFAULT_SECTION, section.uuid)

            db.insert(TABLE_DEFAULT_SECTION, null, values)
        }
        db.close()
    }

    fun addDefaultFields(id: Long, fields: Array<Field>) {

        val db = this.writableDatabase
        fields.forEach { field ->

            val values = ContentValues()
            values.put(ID_FORM_DEFAULT_DEFAULT_FIELD, id)
            values.put(TYPE_DEFAULT_FIELD, field.type)
            values.put(LABEL_DEFAULT_FIELD, field.label)
            values.put(NAME_DEFAULT_FIELD, field.name)
            values.put(REQUIRED_DEFAULT_FIELD, field.required)
            values.put(UUID_DEFAULT_FIELD, field.uuid)

            val fieldId = db.insert(TABLE_DEFAULT_FIELD, null, values)

            field.options?.forEach { option ->

                val optionValues = ContentValues()
                optionValues.put(ID_FIELD_DEFAULT_DEFAULT_OPTION_FIELD, fieldId)
                optionValues.put(LABEL_DEFAULT_OPTION_FIELD, option.label)
                optionValues.put(VALUE_DEFAULT_OPTION_FIELD, option.value)

                db.insert(TABLE_DEFAULT_OPTION_FIELD, null, optionValues)
            }
        }
        db.close()
    }

    fun getDefaultForm(id: Int): Form? {

        val form: Form
        var sections: Array<Section> = arrayOf()
        var fields: Array<Field> = arrayOf()
        var options: Array<Option> = arrayOf()

        val db = this.readableDatabase

        var cursor = db.rawQuery("SELECT * FROM $TABLE_DEFAULT_FORM WHERE id=$id", null)
        if (cursor!!.moveToFirst()) {

            form = Form(id = cursor.getInt(0), title = cursor.getString(1))
            cursor.close()

            cursor =
                db.rawQuery("SELECT * FROM $TABLE_DEFAULT_SECTION WHERE defaultFormId=$id", null)
            if (cursor!!.moveToFirst()) {
                sections += Section(
                    title = cursor.getString(2),
                    from = cursor.getInt(3),
                    to = cursor.getInt(4),
                    index = cursor.getInt(5),
                    uuid = cursor.getString(6)
                )

                while (cursor.moveToNext()) {
                    sections += Section(
                        title = cursor.getString(2),
                        from = cursor.getInt(3),
                        to = cursor.getInt(4),
                        index = cursor.getInt(5),
                        uuid = cursor.getString(6)
                    )
                }
                cursor.close()
            }

            cursor = db.rawQuery("SELECT * FROM $TABLE_DEFAULT_FIELD WHERE defaultFormId=$id", null)
            if (cursor!!.moveToFirst()) {

                var cursor2 = db.rawQuery(
                    "SELECT * FROM $TABLE_DEFAULT_OPTION_FIELD WHERE defaultFieldId=$cursor.getInt(0)",
                    null
                )
                if (cursor2!!.moveToFirst()) {
                    options += Option(label = cursor2.getString(2), value = cursor2.getString(3))
                    while (cursor2.moveToNext()) {
                        options += Option(
                            label = cursor2.getString(2), value = cursor2.getString(3)
                        )
                    }
                }
                cursor2.close()

                fields += Field(
                    type = cursor.getString(2),
                    label = cursor.getString(3),
                    name = cursor.getString(4),
                    required = cursor.getInt(5) == 1,
                    uuid = cursor.getString(6),
                    options = options
                )

                while (cursor.moveToNext()) {

                    options = arrayOf()
                    cursor2 = db.rawQuery(
                        "SELECT * FROM $TABLE_DEFAULT_OPTION_FIELD WHERE defaultFieldId=$cursor.getInt(0)",
                        null
                    )
                    if (cursor2!!.moveToFirst()) {
                        options += Option(
                            label = cursor2.getString(2), value = cursor2.getString(3)
                        )
                        while (cursor2.moveToNext()) {
                            options += Option(
                                label = cursor2.getString(2), value = cursor2.getString(3)
                            )
                        }
                    }
                    cursor2.close()

                    fields += Field(
                        type = cursor.getString(2),
                        label = cursor.getString(3),
                        name = cursor.getString(4),
                        required = cursor.getInt(5) == 1,
                        uuid = cursor.getString(6),
                        options = options
                    )
                }
                cursor.close()
            }

            form.sections = sections
            form.fields = fields
            return form
        }
        db.close()

        return null
    }

    fun getAllForms(): Array<Form> {

        var forms: Array<Form> = arrayOf()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_DEFAULT_FORM", null)
        if (cursor!!.moveToFirst()) {
            forms += Form(id = cursor.getInt(0), title = cursor.getString(1))

            while (cursor.moveToNext()) {
                forms += Form(id = cursor.getInt(0), title = cursor.getString(1))
            }
        }
        cursor.close()
        db.close()

        return forms
    }

    fun getAllEntries(): Array<Record> {

        var records: Array<Record> = arrayOf()
        val db = this.readableDatabase

        val cursor = db.rawQuery(
            "SELECT $TABLE_FORM.id, $TABLE_FORM.defaultFormId, $TABLE_DEFAULT_FORM.title, $TABLE_FORM.title FROM $TABLE_FORM INNER JOIN $TABLE_DEFAULT_FORM WHERE $TABLE_FORM.defaultFormId = $TABLE_DEFAULT_FORM.id",
            null
        )
        if (cursor!!.moveToFirst()) {
            records += Record(
                id = cursor.getInt(0),
                defaultFormId = cursor.getInt(1),
                form = cursor.getString(2),
                title = cursor.getString(3)
            )

            while (cursor.moveToNext()) {
                records += Record(
                    id = cursor.getInt(0),
                    defaultFormId = cursor.getInt(1),
                    form = cursor.getString(2),
                    title = cursor.getString(3)
                )
            }
        }
        cursor.close()
        db.close()

        return records
    }

    fun getEntrieById(id: Int): Record? {
        var record: Record
        val db = this.readableDatabase


        db.close()

        return null
    }

    fun saveEntrie(record: Record): Record {
        var savedRecord: Record = record
        val db = this.writableDatabase

        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_FORM WHERE id=record.id", null)
        if (cursor!!.moveToFirst()) {

            var values = ContentValues()
            values.put(TITLE_FORM, record.title)
            val id = db.insert(TABLE_FORM, null, values)

            record.fields?.forEach { field ->

                values = ContentValues()
                values.put(ID_FORM_FIELD, id)
                values.put(ID_FIELD_FIELD, field.id)
                values.put(VALUE_FIELD, field.value)
                db.insert(TABLE_FIELD, null, values)
            }

        } else {

            var values = ContentValues()
            values.put(TITLE_FORM, record.title)
            db.update(TABLE_FORM, values, "id=?", arrayOf(record.id.toString()))

            record.fields?.forEach { field ->

                values = ContentValues()
                values.put(VALUE_FIELD, field.value)
                db.update(TABLE_FIELD, values, "id=?", arrayOf(field.id.toString()))
            }
        }
        cursor.close()
        db.close()

        return savedRecord
    }

    companion object {
        private const val DATABASE_NAME = "EVALUATION"
        private const val DATABASE_VERSION = 1

        const val TABLE_DEFAULT_FORM = "defaultForms"
        const val ID_DEFAULT_FORM = "id"
        const val TITLE_DEFAULT_FORM = "title"

        const val TABLE_DEFAULT_SECTION = "defaultSections"
        const val ID_DEFAULT_SECTION = "id"
        const val ID_FORM_DEFAULT_DEFAULT_SECTION = "defaultFormId"
        const val TITLE_DEFAULT_SECTION = "title"
        const val FROM_DEFAULT_SECTION = "from_"
        const val TO_DEFAULT_SECTION = "to_"
        const val INDEX_DEFAULT_SECTION = "index_"
        const val UUID_DEFAULT_SECTION = "uuid"

        const val TABLE_DEFAULT_FIELD = "defaultFields"
        const val ID_DEFAULT_FIELD = "id"
        const val ID_FORM_DEFAULT_DEFAULT_FIELD = "defaultFormId"
        const val TYPE_DEFAULT_FIELD = "type"
        const val LABEL_DEFAULT_FIELD = "label"
        const val NAME_DEFAULT_FIELD = "name"
        const val REQUIRED_DEFAULT_FIELD = "required"
        const val UUID_DEFAULT_FIELD = "uuid"

        const val TABLE_DEFAULT_OPTION_FIELD = "defaultOptionsField"
        const val ID_DEFAULT_OPTION_FIELD = "id"
        const val ID_FIELD_DEFAULT_DEFAULT_OPTION_FIELD = "defaultFieldId"
        const val LABEL_DEFAULT_OPTION_FIELD = "label"
        const val VALUE_DEFAULT_OPTION_FIELD = "value"

        const val TABLE_FORM = "forms"
        const val ID_FORM = "id"
        const val ID_FORM_DEFAULT_FORM = "defaultFormId"
        const val TITLE_FORM = "title"

        const val TABLE_FIELD = "fields"
        const val ID_FIELD = "id"
        const val ID_FORM_FIELD = "formId"
        const val ID_FIELD_FIELD = "fieldId"
        const val VALUE_FIELD = "value"

    }
}
