//
//  Form.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 26/07/24.
//

import Foundation

struct Form: Codable {
    var id: Int64? = nil
    var title: String
    var sections: [Section]? = nil
    var fields: [Field]? = nil
}
