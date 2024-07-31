//
//  RadioButtonWithLabelView.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 30/07/24.
//

import Foundation
import UIKit

class RadioButtonWithLabelView: UIView {
    convenience init(field: Field?, self: EntrieViewController) {
        self.init()

        let contentView = UIView()

        contentView.backgroundColor = .white
        contentView.layer.borderWidth = 0.5
        contentView.layer.borderColor = UIColor.lightGray.cgColor
        contentView.layer.cornerRadius = 10;
        contentView.layer.masksToBounds = true;

        let option1 = UIRadioButton()
        let option2 = UIRadioButton()

        let group = UIRadioButtonGroup()
        group.add( option1 )
        group.add( option2 )
        
        //contentView.addSubview(group)
        //textField.translatesAutoresizingMaskIntoConstraints = false
        //textField.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 25).isActive = true
        //textField.centerYAnchor.constraint(equalTo: contentView.centerYAnchor).isActive = true
        //textField.delegate = self

        addSubview(contentView)

        contentView.translatesAutoresizingMaskIntoConstraints = false
        contentView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        contentView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        contentView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        contentView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true

        let label = PaddedLabel()
        label.font = UIFont.systemFont(ofSize: 14.0)
        label.text = field?.label.html2String
        label.backgroundColor = .white
        label.textColor = UIColor.lightGray
        label.textAlignment = .center

        addSubview(label)
        label.translatesAutoresizingMaskIntoConstraints = false
        label.topAnchor.constraint(equalTo: topAnchor, constant: -10).isActive = true
        label.leftAnchor.constraint(equalTo: leftAnchor, constant: 15).isActive = true
    }
}

