//
//  ViewController.swift
//  CPSample
//
//  Created by Jan Freymann on 15.04.19.
//  Copyright © 2019 Jan Freymann. All rights reserved.
//

import UIKit
import SharedCode

class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    @IBOutlet weak var tableView: UITableView!
    
    var todos: [String]?
    let viewModel = TodoViewModel(repo: TodoRepository())
    
    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel.observeTodos(onChangeCallback: { (list: [Todo]) -> KotlinUnit in
            self.todos = list.map { (t) -> String in return t.title }
            self.tableView.reloadData()
            return KotlinUnit()
        })
        syncData(self)
    }
    
    @IBAction func clearData(_ sender: Any) {
        viewModel.clearTodos()
    }
    
    @IBAction func syncData(_ sender: Any) {
        viewModel.triggerSync()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard let todoList = todos else {
            return 0
        }
        
        return todoList.count
    }
    
    deinit {
        viewModel.onDestroy()
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! TableViewCell
        
        cell.label.text = todos![indexPath.row]
        return cell
    }
}

