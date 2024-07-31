package nl.cloudplusplus.evaluation.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import nl.cloudplusplus.evaluation.models.Field
import nl.cloudplusplus.evaluation.models.Section

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        var query = ("CREATE TABLE " + TABLE_DEFAULT_FORM + " ("
                + ID_DEFAULT_FORM + " INTEGER PRIMARY KEY, " +
                TITLE_DEFAULT_FORM + " TEXT" +
                ")")
        db.execSQL(query)

        query = ("CREATE TABLE " + TABLE_DEFAULT_SECTION + " ("
                + ID_DEFAULT_SECTION + " INTEGER PRIMARY KEY, " +
                ID_FORM_DEFAULT_DEFAULT_SECTION + " INTEGER," +
                TITLE_DEFAULT_SECTION + " TEXT," +
                FROM_DEFAULT_SECTION + " INTEGER," +
                TO_DEFAULT_SECTION + " INTEGER," +
                INDEX_DEFAULT_SECTION + " INTEGER," +
                UUID_DEFAULT_SECTION + " TEXT" +
                ")")
        db.execSQL(query)

        query = ("CREATE TABLE " + TABLE_DEFAULT_FIELD + " ("
                + ID_DEFAULT_FIELD + " INTEGER PRIMARY KEY, " +
                ID_FORM_DEFAULT_DEFAULT_FIELD + " INTEGER," +
                TYPE_DEFAULT_FIELD + " TEXT," +
                LABEL_DEFAULT_FIELD + " TEXT," +
                NAME_DEFAULT_FIELD + " TEXT," +
                REQUIRED_DEFAULT_FIELD + " INTEGER," +
                UUID_DEFAULT_FIELD + " TEXT" +
                ")")
        db.execSQL(query)

        query = ("CREATE TABLE " + TABLE_DEFAULT_OPTION_FIELD + " ("
                + ID_DEFAULT_OPTION_FIELD + " INTEGER PRIMARY KEY, " +
                ID_FIELD_DEFAULT_DEFAULT_OPTION_FIELD + " INTEGER," +
                LABEL_DEFAULT_OPTION_FIELD + " TEXT," +
                VALUE_DEFAULT_OPTION_FIELD + " TEXT" +
                ")")
        db.execSQL(query)

        query = ("CREATE TABLE " + TABLE_FORM + " ("
                + ID_FORM + " INTEGER PRIMARY KEY, " +
                ID_FORM_DEFAULT_FORM + " INTEGER," +
                TITLE_FORM + " TEXT" +
                ")")
        db.execSQL(query)

        query = ("CREATE TABLE " + TABLE_FIELD + " ("
                + ID_FIELD + " INTEGER PRIMARY KEY, " +
                ID_FORM_FIELD + " INTEGER," +
                ID_FIELD_FIELD + " INTEGER," +
                VALUE_FIELD + " TEXT" +
                ")")
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
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_DEFAULT_FORM", null)
        cursor!!.moveToFirst()
        val hasRow = cursor.getInt(0) == 0
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

    fun getAllForms(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_DEFAULT_FORM", null)
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
