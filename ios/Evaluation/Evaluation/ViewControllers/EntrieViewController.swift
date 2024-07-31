//
//  EntrieViewController.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 29/07/24.
//

import UIKit

class EntrieViewController: UITableViewController {
    
    var selectedEntrie: Record?
    let dataAccess = DataAccess()
    
    @IBAction func Save(_ sender: Any) {
        do {
            let savedRecord = try dataAccess.saveEntrie(selectedEntrie!)
            selectedEntrie?.id = savedRecord.id
        } catch {
            print(error)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        UITableView.appearance().isPrefetchingEnabled = false
        
        if (selectedEntrie?.id == nil) {
            do {
                let form = try dataAccess.getDefaultForm(selectedEntrie?.defaultFormId ?? 0)
                selectedEntrie?.form = form?.title
                selectedEntrie?.title = randomString(length: 32)
                selectedEntrie?.fields = form?.fields
                selectedEntrie?.sections = form?.sections
                
            } catch {
                print(error)
            }
        } else {
            do {
                selectedEntrie = try dataAccess.getEntrieById(selectedEntrie?.id ?? 0)
            } catch {
                print(error)
            }
        }
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return self.selectedEntrie?.sections!.count ?? 1
    }
        
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return ((self.selectedEntrie?.sections![section].to ?? 0) - (self.selectedEntrie?.sections![section].from ?? 0)) + 1
    }
    
    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 70
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return self.selectedEntrie?.sections![section].title.html2String
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DetailCell", for: indexPath)
        
        cell.contentView.subviews.forEach { subView in
            subView.removeFromSuperview()
        }
        
        cell.backgroundColor = .white
        cell.selectionStyle = .none
        cell.contentView.isUserInteractionEnabled = true
        cell.contentView.translatesAutoresizingMaskIntoConstraints = true
        cell.isEditing = true
        
        let field = self.selectedEntrie?.fields![indexPath.item]
        
        switch field?.type {
        case "text", "email", "number", "date", "description", "textarea", "dropdown":
            let textField = TextFieldWithLabelView(tag: indexPath.item, field: field, self: self)
            cell.contentView.addSubview(textField)
            textField.translatesAutoresizingMaskIntoConstraints = false
            textField.topAnchor.constraint(equalTo: cell.topAnchor, constant: 20).isActive = true
            textField.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 20).isActive = true
            textField.heightAnchor.constraint(equalToConstant: self.selectedEntrie?.fields![indexPath.item].type == "description" || self.selectedEntrie?.fields![indexPath.item].type == "textarea"  ? 170 : 60).isActive = true
            textField.widthAnchor.constraint(equalToConstant: UIScreen.main.bounds.size.width - 40).isActive = true

        default:
            cell.textLabel?.text = self.selectedEntrie?.fields![indexPath.item].label.html2String
        }

        return cell
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return self.selectedEntrie?.fields![indexPath.item].type == "text" || self.selectedEntrie?.fields![indexPath.item].type == "email" || self.selectedEntrie?.fields![indexPath.item].type == "number" ||  self.selectedEntrie?.fields![indexPath.item].type == "date" || self.selectedEntrie?.fields![indexPath.item].type == "radio" || self.selectedEntrie?.fields![indexPath.item].type == "dropdown" ? 90 : self.selectedEntrie?.fields![indexPath.item].type == "description" || self.selectedEntrie?.fields![indexPath.item].type == "textarea"  ? 200 : 80;
    }
}

extension EntrieViewController: UITextFieldDelegate {
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let field = selectedEntrie?.fields?[textField.tag]
        if (field?.type == "number") {
            let allowedCharacters = CharacterSet.decimalDigits
            let characterSet = CharacterSet(charactersIn: string)
            return allowedCharacters.isSuperset(of: characterSet)
        }
        return true
    }

    func textFieldDidEndEditing(_ textField: UITextField) {
        selectedEntrie?.fields?[textField.tag].value = textField.text!
    }
}
