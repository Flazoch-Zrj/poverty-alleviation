var storage = require('./utils/storage')
var API_BASE_URL = 'http://localhost:8080/api/v1'
App({
  globalData: { userInfo: null, baseUrl: API_BASE_URL },
  onLaunch: function() {
    var token = storage.getToken()
    if (token) {
      var that = this
      wx.request({
        url: that.globalData.baseUrl + '/system/auth/check',
        header: { Authorization: 'Bearer ' + token },
        success: function(res) {
          if (res.data && res.data.code === 200) that.globalData.userInfo = res.data.data
          else storage.removeToken()
        },
        fail: function() { storage.removeToken() }
      })
    }
  }
})
