//
//  Record.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 29/07/24.
//

import Foundation

struct Record: Codable {
    var id: Int64? = nil
    var defaultFormId: Int64
    var form: String? = nil
    var title: String? = nil
    var sections: [Section]? = nil
    var fields: [Field]? = nil
}
