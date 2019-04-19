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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        helloWorldLabel.text = PlatformKt.getPlatform()
        
    }


}

