//
//  DataAccess.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 29/07/24.
//

import Foundation
import SQLite

class DataAccess {
    
    func fileName() -> String {
        let path = NSSearchPathForDirectoriesInDomains(
            .documentDirectory, .userDomainMask, true
        ).first!
        
        //print(path)
        
        let name = "\(path)/db.sqlite3";
        return name;
    }
    
    func initTables() throws {
        do {
            let db = try Connection(fileName())
            try initDefaultFormTable(db: db)
            try initDefaultSectionTable(db: db)
            try initDefaultFieldTable(db: db)
            try initDefaultOptionsFieldTable(db: db)
            try initFormTable(db: db)
            try initFieldTable(db: db)
        } catch {
           throw error
        }
    }
    
    private func initDefaultFormTable(db: Connection) throws {
        let defaultForms = Table("defaultForms")
        let id = Expression<Int64>("id")
        let title = Expression<String>("title")
        
        try db.run(defaultForms.create { t in
            t.column(id, primaryKey: true)
            t.column(title)
        })
    }

    private func initDefaultSectionTable(db: Connection) throws {
        let defaultSections = Table("defaultSections")
        let id = Expression<Int64>("id")
        let defaultFormId = Expression<Int64>("defaultFormId")
        let title = Expression<String>("title")
        let from = Expression<Int>("from")
        let to = Expression<Int>("to")
        let index = Expression<Int>("index")
        let uuid = Expression<String>("uuid")

        try db.run(defaultSections.create { t in
            t.column(id, primaryKey: true)
            t.column(defaultFormId)
            t.column(title)
            t.column(from)
            t.column(to)
            t.column(index)
            t.column(uuid)
        })
    }

    private func initDefaultFieldTable(db: Connection) throws {
        let defaultFields = Table("defaultFields")
        let id = Expression<Int64>("id")
        let defaultFormId = Expression<Int64>("defaultFormId")
        let type = Expression<String>("type")
        let label = Expression<String>("label")
        let name = Expression<String>("name")
        let required = Expression<Bool?>("required")
        let uuid = Expression<String>("uuid")
        
        try db.run(defaultFields.create { t in
            t.column(id, primaryKey: true)
            t.column(defaultFormId)
            t.column(type)
            t.column(label)
            t.column(name)
            t.column(required)
            t.column(uuid)
        })
    }
    
    private func initDefaultOptionsFieldTable(db: Connection) throws {
        let defaultOptionsField = Table("defaultOptionsField")
        let id = Expression<Int64>("id")
        let defaultFieldId = Expression<Int64>("defaultFieldId")
        let label = Expression<String>("label")
        let value = Expression<String>("value")
        
        try db.run(defaultOptionsField.create { t in
            t.column(id, primaryKey: true)
            t.column(defaultFieldId)
            t.column(label)
            t.column(value)
        })
    }
    
    private func initFormTable(db: Connection) throws {
        let forms = Table("forms")
        let id = Expression<Int64>("id")
        let defaultFormId = Expression<Int64>("defaultFormId")
        let title = Expression<String>("title")
        
        try db.run(forms.create { t in
            t.column(id, primaryKey: true)
            t.column(defaultFormId)
            t.column(title)
        })
    }
    
    private func initFieldTable(db: Connection) throws {
        let fields = Table("fields")
        let id = Expression<Int64>("id")
        let formId = Expression<Int64>("formId")
        let fieldId = Expression<Int64>("fieldId")
        let value = Expression<String?>("value")
        
        try db.run(fields.create { t in
            t.column(id, primaryKey: true)
            t.column(formId)
            t.column(fieldId)
            t.column(value)
        })
    }

    func getDefaultForm(_ id: Int64) throws -> Form?  {
        let db = try Connection(fileName())
        do {
            guard let queryResults = try? db.prepare("SELECT * FROM defaultForms WHERE id=\(id)") else { return nil }
            for row in queryResults {
                
                var sections : [Section] = []
                var fields : [Field] = []
                var options : [Option] = []

                guard let queryResults1 = try? db.prepare("SELECT * FROM defaultSections WHERE defaultFormId=\(row[0] as! Int64)") else { return nil }
                for row1 in queryResults1 {
                    let from = row1[3] as! Int64
                    let to = row1[4] as! Int64
                    let index = row1[5] as! Int64
                    sections.append(Section(title: row1[2] as! String, from: Int(from) , to: Int(to), index: Int(index), uuid: row1[6] as! String))
                }

                guard let queryResults2 = try? db.prepare("SELECT * FROM defaultFields WHERE defaultFormId=\(row[0] as! Int64)") else { return nil }
                for row2 in queryResults2 {
                    
                    guard let queryResults3 = try? db.prepare("SELECT * FROM defaultOptionsField WHERE defaultFieldId=\(row2[0] as! Int64)") else { return nil }
                    for row3 in queryResults3 {
                        options.append(Option(label: row3[2] as! String, value: row3[3] as! String))
                    }

                    fields.append(Field(id: row2[0] as? Int64, type: row2[2] as! String, label: row2[3] as! String, name: row2[4] as! String, uuid: row2[6] as! String, options: options))
                }
                
                return Form(id: row[0] as? Int64, title: row[1] as! String, sections: sections, fields: fields)
            }
        }
        return nil;
    }
    
    func getAllForms() throws -> [Form] {
        let db = try Connection(fileName())
        var forms : [Form] = []
        do {
            guard let queryResults = try? db.prepare("SELECT id, title FROM defaultForms") else { return [] }
            for row in queryResults {
                forms.append(Form(id: row[0] as? Int64, title: row[1] as! String))
            }
        }
        return forms
    }
    
    func getAllEntries() throws -> [Record] {
        let db = try Connection(fileName())
        var records : [Record] = []
        do {
            guard let queryResults = try? db.prepare("SELECT forms.id, forms.defaultFormId,  defaultForms.title, forms.title FROM forms INNER JOIN defaultForms WHERE forms.defaultFormId = defaultForms.id") else { return [] }
            for row in queryResults {
                records.append(Record(id: row[0] as? Int64, defaultFormId: row[1] as! Int64, form: row[2] as? String, title: row[3] as? String))
            }
        }
        return records
    }

    func getEntrieById(_ id: Int64) throws -> Record? {
        let db = try Connection(fileName())
        do {
            guard let queryResults = try? db.prepare("SELECT * FROM forms WHERE id=\(id)") else { return nil }
            for row in queryResults {
                
                let id = row[0] as! Int64
                let defaultFormId = row[1] as! Int64
                let title = row[2] as! String
                let form : Form? = try self.getDefaultForm(defaultFormId)
                
                var fields : [Field] = []
                var options : [Option] = []
                
                guard let queryResults1 = try? db.prepare("SELECT * FROM fields INNER JOIN defaultFields WHERE fields.fieldId=defaultFields.id AND fields.formId=\(id)") else { return nil }
                for row1 in queryResults1 {
                    
                    print(row1[0] as! Int64)
                    print("SELECT * FROM defaultOptionsField WHERE defaultFieldId=\(row1[0] as! Int64)")
                    
                    guard let queryResults2 = try? db.prepare("SELECT * FROM defaultOptionsField WHERE defaultFieldId=\(row1[0] as! Int64)") else { return nil }
                    for row2 in queryResults2 {
                        
                        print("option")
                        print(row2)
                        
                        options.append(Option(label: row2[2] as! String, value: row2[3] as! String))
                    }

                    fields.append(Field(id: row1[4] as? Int64, type: row1[6] as! String, label: row1[7] as! String, name: row1[8] as! String, uuid: row1[10] as! String, options: options, fieldId: row1[0] as? Int64, value: row1[3] as? String))
                }
                
                return Record(id: id, defaultFormId: defaultFormId, form: form?.title,  title: title, sections: form?.sections, fields: fields)
            }
        }
        return nil;
    }
    
    func saveEntrie(_ record: Record) throws -> Record {
        let db = try Connection(fileName())
        var savedRecord = record
        do {
            let queryResults = try? db.prepare("SELECT count(*) FROM forms WHERE id=\(record.id ?? 0)")
            for row in queryResults! {
                let exist = row[0] as! Int64
                if exist == 0 {
                    
                    let stmt = try db.prepare("INSERT INTO forms (defaultFormId, title) VALUES (?,?)")
                    try stmt.run(record.defaultFormId ,record.title)
                    let lastRowId = db.lastInsertRowid

                    savedRecord.id = lastRowId
                    
                    let stmt2 = try db.prepare("INSERT INTO fields (formId, fieldId, value) VALUES (?,?,?)")
                    try record.fields?.forEach { field in
                        try stmt2.run(lastRowId, field.id, field.value)
                    }
                    
                } else {
                    
                    let stmt = try db.prepare("UPDATE forms SET title=? WHERE id=?")
                    try stmt.run(record.title, record.id)
                    
                    let stmt2 = try db.prepare("UPDATE fields SET value=? WHERE id=?")
                    try record.fields?.forEach { field in
                        try stmt2.run(field.value, field.fieldId)
                    }
                }
            }
        }
        return savedRecord
    }
    
    init() {
        
        let fileManager = FileManager.default
        if !fileManager.fileExists(atPath: fileName()) {
            do {
                try initTables()
                
                let db = try Connection(fileName())
                if let data = readLocalJSONFile(forName: "200-form")
                {
                    let form = try JSONDecoder().decode(Form.self, from: data)
                    
                    let stmt = try db.prepare("INSERT INTO defaultForms (title) VALUES (?)")
                    try stmt.run(form.title)
                    let lastRowId = db.lastInsertRowid
                    
                    let stmt2 = try db.prepare("INSERT INTO defaultSections (defaultFormId, title, 'from', 'to', 'index', uuid) VALUES (?,?,?,?,?,?)")
                    try form.sections?.forEach { section in
                        try stmt2.run(lastRowId, section.title, section.from, section.to, section.index, section.uuid)
                     }

                    let stmt3 = try db.prepare("INSERT INTO defaultFields (defaultFormId, type, label, name, required, uuid) VALUES (?,?,?,?,?,?)")
                    try form.fields?.forEach { field in
                        try stmt3.run(lastRowId, field.type, field.label, field.name, field.required, field.uuid)
                        let lastRowId = db.lastInsertRowid
                        
                        if (field.type == "checkbox" || field.type == "dropdown" || field.type == "radio") {
                            let stmt4 = try db.prepare("INSERT INTO defaultOptionsField (defaultFieldId, label, value) VALUES (?,?,?)")
                            try field.options?.forEach { option in
                                try stmt4.run(lastRowId, option.label, option.value)
                            }
                        }
                    }
                }
                if let data = readLocalJSONFile(forName: "all-fields")
                {
                    let form = try JSONDecoder().decode(Form.self, from: data)
                    
                    let stmt = try db.prepare("INSERT INTO defaultForms (title) VALUES (?)")
                    try stmt.run(form.title)
                    let lastRowId = db.lastInsertRowid
                    
                    let stmt2 = try db.prepare("INSERT INTO defaultSections (defaultFormId, title, 'from', 'to', 'index', uuid) VALUES (?,?,?,?,?,?)")
                    try form.sections?.forEach { section in
                        try stmt2.run(lastRowId, section.title, section.from, section.to, section.index, section.uuid)
                     }
                    
                    let stmt3 = try db.prepare("INSERT INTO defaultFields (defaultFormId, type, label, name, required, uuid) VALUES (?,?,?,?,?,?)")
                    try form.fields?.forEach { field in
                        try stmt3.run(lastRowId, field.type, field.label, field.name, field.required, field.uuid)
                        let lastRowId = db.lastInsertRowid
                        
                        if (field.type == "checkbox" || field.type == "dropdown" || field.type == "radio") {
                            let stmt4 = try db.prepare("INSERT INTO defaultOptionsField (defaultFieldId, label, value) VALUES (?,?,?)")
                            try field.options?.forEach { option in
                                try stmt4.run(lastRowId, option.label, option.value)
                            }
                        }
                     }
                }
            } catch {
                print(error)
            }
        }
    }
}
