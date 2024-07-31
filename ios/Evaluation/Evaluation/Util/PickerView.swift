//
//  PickerView.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 30/07/24.
//

import UIKit

class PickerView: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource, UITextFieldDelegate {
    
    var textField: UITextField?
    var options : [Option]?
    
    convenience init(textField: UITextField, options: [Option]) {
        self.init()
        self.textField = textField
        self.options = options
        self.createPickerView()
        self.dismissPickerView()
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return self.options!.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return self.options![row].label
       
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        textField!.text = self.options![row].value
    }
    
    func createPickerView() {
        let pickerView = UIPickerView()
        pickerView.delegate = self
        textField!.inputView = pickerView
    }
    
    func dismissPickerView() {
        let toolBar = UIToolbar()
        toolBar.sizeToFit()
        
        let button = UIBarButtonItem(title: "Done", style: .plain, target: self, action: #selector(action))
        toolBar.setItems([button], animated: true)
        toolBar.isUserInteractionEnabled = true
        textField!.inputAccessoryView = toolBar
    }
    
    @objc func action() {
        view.endEditing(true)
    }
}
