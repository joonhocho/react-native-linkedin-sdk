const ReactNative = require('react-native');

const {
  NativeModules,
} = ReactNative;


const {
  RNLinkedInSessionManager,
} = NativeModules;


const LinkedInSDK = {
  configure(config) {
    return RNLinkedInSessionManager.configure(config);
  },
  authorize() {
    return RNLinkedInSessionManager.authorize();
  },
  signIn(config) {
    LinkedInSDK.configure(config);
    return LinkedInSDK.authorize()
      .then(LinkedInSDK.normalizeToken);
  },
  normalizeToken({expiresOn, ...token}) {
    return {
      expiresOn: new Date(expiresOn * 1000),
      ...token,
    };
  },
  getRequest(url) {
    return RNLinkedInSessionManager.getRequest(url);
  },
};

module.exports = LinkedInSDK;
