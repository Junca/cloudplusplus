//
//  TextFieldWithLabel.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 30/07/24.
//

import Foundation
import UIKit

class PaddedLabel: UILabel {
    override var intrinsicContentSize: CGSize {
        CGSize(width: super.intrinsicContentSize.width + 20, height: super.intrinsicContentSize.height)
    }
}

class TextFieldWithLabelView: UIView {
    convenience init(tag: Int, field: Field?, self: EntrieViewController) {
        self.init()

        let contentView = UIView()

        contentView.backgroundColor = .white
        contentView.layer.borderWidth = 0.5
        contentView.layer.borderColor = UIColor.lightGray.cgColor
        contentView.layer.cornerRadius = 10;
        contentView.layer.masksToBounds = true;

        let textField = UITextField()
        textField.textColor = .black
        textField.font = UIFont.systemFont(ofSize: 14.0)
        textField.text = field?.value
        textField.placeholder = field?.name
        textField.tag = tag

        contentView.addSubview(textField)
        textField.translatesAutoresizingMaskIntoConstraints = false
        textField.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: 25).isActive = true
        textField.centerYAnchor.constraint(equalTo: contentView.centerYAnchor).isActive = true
        textField.delegate = self
        
        if field?.type == "date" {
            textField.datePicker(target: textField,
                                 doneAction: #selector(textField.datePickerDoneAction),
                                 cancelAction: #selector(textField.cancelAction),
                                 datePickerMode: .date)
        } else if field?.type == "dropdown" {
            //textField.allowsEditingTextAttributes = false
            _ = PickerView(textField: textField, options: (field?.options)!)
        }

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

extension UITextField {
    
    @objc func datePickerDoneAction() {
        if let datePicker = self.inputView as? UIDatePicker {
            let dateformatter = DateFormatter()
            dateformatter.dateStyle = .medium
            dateformatter.dateFormat = "dd/MM/yyyy"
            self.text = dateformatter.string(from: datePicker.date)
        }
        self.resignFirstResponder()
    }
        
    @objc func cancelAction() {
        self.resignFirstResponder()
    }
    
   func datePicker<T>(target: T,
                   doneAction: Selector,
                   cancelAction: Selector,
                   datePickerMode: UIDatePicker.Mode = .date) {

       let screenWidth = UIScreen.main.bounds.width
    
       func buttonItem(withSystemItemStyle style: UIBarButtonItem.SystemItem) -> UIBarButtonItem {
           let buttonTarget = style == .flexibleSpace ? nil : target
           let action: Selector? = {
               switch style {
               case .cancel:
                   return cancelAction
               case .done:
                   return doneAction
               default:
                   return nil
               }
           }()
        
           let barButtonItem = UIBarButtonItem(barButtonSystemItem: style,
                                            target: buttonTarget,
                                            action: action)
        
           return barButtonItem
       }
    
       let datePicker = UIDatePicker(frame: CGRect(x: 0,
                                                   y: 0,
                                                   width: screenWidth,
                                                   height: 216))
       datePicker.datePickerMode = datePickerMode
       datePicker.preferredDatePickerStyle = .wheels
       
       let dateFormatter = DateFormatter()
       dateFormatter.dateFormat =  "dd/MM/yyyy"
       if let date = dateFormatter.date(from: self.text!) {
           datePicker.date = date
       }
       self.inputView = datePicker
    
       let toolBar = UIToolbar(frame: CGRect(x: 0,
                                             y: 0,
                                             width: screenWidth,
                                             height: 44))
       toolBar.setItems([buttonItem(withSystemItemStyle: .cancel),
                         buttonItem(withSystemItemStyle: .flexibleSpace),
                         buttonItem(withSystemItemStyle: .done)],
                        animated: true)
       self.inputAccessoryView = toolBar
    }
}
