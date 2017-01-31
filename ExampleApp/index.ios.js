/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight
} from 'react-native';
import LinkedInSDK from 'react-native-linkedin-sdk';

export default class ExampleApp extends Component {
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to React Native!
        </Text>
        <Text style={styles.instructions}>
          To get started, edit index.ios.js
        </Text>
        <Text style={styles.instructions}>
          Press Cmd+R to reload,{'\n'}
          Cmd+D or shake for dev menu
        </Text>
        <TouchableHighlight onPress={async () => {
          const token = await LinkedInSDK.signIn({
            // https://developer.linkedin.com/docs/oauth2

            // iOS (Required)
            // The "API Key" value generated when you registered your application.
            clientID: '86ltc4i0wckzvo',

            // iOS (Required)
            clientSecret: 'KCPHGyyXOjmGy72S',

            // iOS (Required)
            // A unique string value of your choice that is hard to guess. Used to prevent CSRF.
            state: 'abcde',

            // iOS, Android (Required)
            scopes: [
              'r_basicprofile',
            ],

            // iOS (Required)
            // The URI your users will be sent back to after authorization.  This value must match one of the defined OAuth 2.0 Redirect URLs in your application configuration.
            // e.g. https://www.example.com/auth/linkedin
            redirectUri: 'https://github.com/joonhocho/react-native-linkedin-sdk/oauth2callback',
          });

          const profile = await LinkedInSDK.getRequest('https://api.linkedin.com/v1/people/~?format=json');

          setTimeout(() => {
            alert(JSON.stringify({token, profile}, null, '  '));
          }, 1500);
        }}>
          <Text style={styles.instructions}>
            LinkedIn Sign-In
          </Text>
        </TouchableHighlight>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('ExampleApp', () => ExampleApp);
