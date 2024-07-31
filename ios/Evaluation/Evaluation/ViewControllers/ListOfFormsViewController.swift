//
//  ViewController.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 26/07/24.
//

import UIKit

class ListOfFormsViewController: UITableViewController {
    
    var forms : [Form] = []
    let dataAccess = DataAccess()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        do {
            self.forms = try dataAccess.getAllForms()
        } catch {
            print(error)
        }
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
        
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.forms.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "FormCell", for: indexPath)
        
        cell.textLabel?.text = self.forms[indexPath.item].title
        return cell
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "LIST OF FORMS"
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "listOfEntries" {
            if let indexPaths = tableView.indexPathForSelectedRow{
                let destinationController = segue.destination as! ListOfFormEntriesViewController
                destinationController.selectedForm = self.forms[indexPaths.row]
                
            }
        }
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.cellForRow(at: indexPath)?.setSelected(false, animated: true)
        performSegue(withIdentifier: "listOfEntries", sender: nil)
    }
}

