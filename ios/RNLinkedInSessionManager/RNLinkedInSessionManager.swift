//
//  RNLinkedInSignIn.swift
//  DropCard
//
//  Created by Joon Ho Cho on 1/18/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

import Foundation

@objc(RNLinkedInSessionManager)
class RNLinkedInSessionManager: NSObject {
  
  static let sharedInstance = RNLinkedInSessionManager()
  
  var linkedinHelper: LinkedinSwiftHelper?
  
  @objc func configure(_ config: [String: Any]) {
    let clientID = config["clientID"] as! String
    let clientSecret = config["clientSecret"] as! String
    let state = config["state"] as! String
    let scopes = config["scopes"] as! [String]
    let redirectUri = config["redirectUri"] as! String
    
    linkedinHelper = LinkedinSwiftHelper(configuration:
      LinkedinSwiftConfiguration(
        clientId: clientID,
        clientSecret: clientSecret,
        state: state,
        permissions: scopes,
        redirectUrl: redirectUri
      )
    )
  }
  
  @objc func authorize(_ resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
    if let linkedinHelper = linkedinHelper {
      DispatchQueue.main.async {
        linkedinHelper.authorizeSuccess({ (lsToken) in
          // Delay is necessary as ViewController is closing.
          DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            resolve(RNLinkedInSessionManager.tokenToJSON(lsToken))
          }
        }, error: { (error) in
          reject("authorizeSuccessError", error.localizedDescription, error)
        }, cancel: {
          reject("authorizeSuccessCancel", "", nil)
        })
      }
    } else {
      reject("uninitialized", "call configure first", nil)
    }
  }
  
  static func tokenToJSON(_ token: LSLinkedinToken?) -> [String: Any]? {
    if let token = token {
      var body: [String: Any] = [:]
      
      if let accessToken = token.accessToken {
        body["accessToken"] = accessToken
      }
      
      if let expiration = token.expireDate {
        body["expiresOn"] = expiration.timeIntervalSince1970
      }
      
      body["fromMobileSDK"] = token.isFromMobileSDK
      
      return body
    } else {
      return nil
    }
  }
  
  @objc func getRequest(_ url: NSString, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
    if let linkedinHelper = linkedinHelper {
      linkedinHelper.requestURL(
        url as String,
        requestType: LinkedinSwiftRequestGet,
        success: { (response) in
          resolve([
            "status": response.statusCode,
            "data": response.jsonObject,
            ])
      },
        error: { (error) in
          reject("error", "", error)
      })
    } else {
      reject("uninitialized", "call configure first", nil)
    }
  }
  
}
