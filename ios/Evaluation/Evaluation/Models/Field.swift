//
//  Field.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 26/07/24.
//

import Foundation

struct Option: Codable {
    var label: String
    var value: String
}

struct Field: Codable {
    var id: Int64? = nil
    var type: String
    var label: String
    var name: String
    var required: Bool? = nil
    var uuid: String
    var options: [Option]? = nil
    
    var fieldId: Int64? = nil
    var value: String? = nil
}
