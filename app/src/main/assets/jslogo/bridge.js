var u = navigator.userAgent;
var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1;
var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);

/*eslint-disable no-unused-vars */
function setupWebViewJavascriptBridge (callback) {
  if (isAndroid) {
    if (window.WebViewJavascriptBridge) {
      return callback(window.WebViewJavascriptBridge)
    }
  } else {
    if (window.WKWebViewJavascriptBridge) {
      return callback(window.WKWebViewJavascriptBridge)
    }
  }
  if (window.WVJBCallbacks) {
    return window.WVJBCallbacks.push(callback)
  }
  window.WVJBCallbacks = [callback];
  var WVJBIframe = document.createElement('iframe');
  WVJBIframe.style.display = 'none';
  WVJBIframe.src = 'wvjbscheme://__BRIDGE_LOADED__';
  document.documentElement.appendChild(WVJBIframe);
  setTimeout(function () {
    document.documentElement.removeChild(WVJBIframe)
  }, 0)
}

function connectWebViewJavascriptBridge (callback) {
  if (isAndroid) {
    if (window.WebViewJavascriptBridge) {
      callback(window.WebViewJavascriptBridge)
    } else {
      document.addEventListener(
          'WebViewJavascriptBridgeReady'
          , function () {
            callback(window.WebViewJavascriptBridge)
          },
          false
      )
    }
  } else {
    if (window.WKWebViewJavascriptBridge) {
      callback(window.WKWebViewJavascriptBridge)
    } else {
      document.addEventListener(
          'WKWebViewJavascriptBridgeReady'
          , function () {
            callback(window.WKWebViewJavascriptBridge)
          },
          false
      )
    }
  }
}
var browser = {
    versions: function () {
        var u = navigator.userAgent, app = navigator.appVersion;
        return {//移动终端浏览器版本信息
            trident: u.indexOf('Trident') > -1, //IE内核
            presto: u.indexOf('Presto') > -1, //opera内核
            webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
            mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
            iPhone: u.indexOf('iPhone') > -1 , //是否为iPhone或者QQHD浏览器
            iPad: u.indexOf('iPad') > -1, //是否iPad
            webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
        }
    }(),
    language: (navigator.browserLanguage || navigator.language).toLowerCase()
};
var appInitCompleted = function (callback) {
    if (browser.versions.android) {
      connectWebViewJavascriptBridge(function (bridge) {
          window.webBridge = bridge;
          bridge.init(function (message, responseCallback) {
              var data = {
                  'Javascript Responds': 'test!'
              }
              responseCallback(data)
          })
          callback(bridge)
      })
    } else if (browser.versions.ios) {
      setupWebViewJavascriptBridge(function (bridge) {
          window.webBridge = bridge;
          callback(bridge)
      })
    } else { // web
      callback()
    }
};