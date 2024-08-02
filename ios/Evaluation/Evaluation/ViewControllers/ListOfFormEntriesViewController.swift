//
//  ListOfFormEntriesViewController.swift
//  Evaluation
//
//  Created by Rodrigo Junqueira on 28/07/24.
//

import UIKit

class ListOfFormEntriesViewController: UITableViewController {
    
    var selectedForm: Form?
    var records : [Record] = []
    let dataAccess = DataAccess()
    
    @IBAction func NewEntrie(_ sender: Any) {
        let destinationController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "EntrieViewController") as! EntrieViewController
        destinationController.selectedEntrie = Record(defaultFormId: selectedForm?.id ?? 0)
        self.present(destinationController, animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        do {
            self.records = try dataAccess.getAllEntries((selectedForm?.id)!)
        } catch {
            print(error)
        }
    }
        
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
        
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.records.count
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "ENTRIES OF \(selectedForm?.title ?? "")"
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "EntrieCell", for: indexPath)
        
        cell.textLabel?.text = self.records[indexPath.item].title
        return cell
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "entrie" {
            if let indexPaths = tableView.indexPathForSelectedRow{
                let destinationController = segue.destination as! EntrieViewController
                destinationController.selectedEntrie = self.records[indexPaths.row]
            }
        }
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.cellForRow(at: indexPath)?.setSelected(false, animated: true)
        performSegue(withIdentifier: "entrie", sender: nil)
    }
}
