//
//  RNLinkedInSessionManagerBridge.m
//  DropCard
//
//  Created by Joon Ho Cho on 1/18/17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <React/RCTBridgeModule.h>
#import <LinkedInSwift/LSHeader.h>

@interface RCT_EXTERN_MODULE(RNLinkedInSessionManager, NSObject)

RCT_EXTERN_METHOD(configure:(NSDictionary *)config);
RCT_EXTERN_METHOD(authorize:
                  (RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject);
RCT_EXTERN_METHOD(getRequest:(NSString *)url
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject);


@end
