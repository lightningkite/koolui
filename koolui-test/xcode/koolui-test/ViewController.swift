//
//  ViewController.swift
//  koolui-test
//
//  Created by Joseph Ivie on 4/8/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import UIKit
import FromKotlin

class ViewController: UIViewController {
    
    @IBOutlet weak var helloWorldLabel: UILabel!
    
    var sleeve: Any? = nil
    weak var button: UIButton? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        helloWorldLabel.text = UIPlatform.ios.name
        let button = UIButton(frame: .zero)
        self.button = button
        button.setTitleColor(UIColor.red, for: .normal)
        button.setTitle("asdf", for: .normal)
        let sleeve = ClosureSleeveKt.makeSleeve {
            print("Hello from closure sleeve")
        }
        print("Sleeve: \(sleeve)")
        self.sleeve = sleeve
        view.addSubview(button)
        button.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
        print(button.frame)
        button.addTarget(self, action: #selector(testPress), for: .touchUpInside)
        button.addTarget(sleeve, action: NSSelectorFromString("runContainedClosure"), for: .touchUpInside)
    }
    
    @objc func testPress(){
        print("Hello from swift test")
    }
    
    
}
