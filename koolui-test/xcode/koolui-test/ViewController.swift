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
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return UIStatusBarStyle.lightContent
    }
}
