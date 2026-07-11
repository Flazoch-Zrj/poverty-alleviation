var app = getApp()
var sto = require('./storage')
function req(opts) {
  var url = opts.url, method = opts.method || 'GET', data = opts.data
  if (opts.showLoading !== false) wx.showLoading({ title: '加载中...', mask: true })
  return new Promise(function(resolve, reject) {
    wx.request({
      url: app.globalData.baseUrl + url, method: method, data: data,
      header: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + (sto.getToken() || '') },
      success: function(r) {
        if (opts.showLoading !== false) wx.hideLoading()
        if (r.statusCode === 401) { sto.removeToken(); wx.redirectTo({ url: '/pages/login/index' }); reject(r.data); return }
        if (r.data && r.data.code === 200) { resolve(r.data) }
        else { wx.showToast({ title: r.data?.message || '请求失败', icon: 'none' }); reject(r.data) }
      },
      fail: function() { if (opts.showLoading !== false) wx.hideLoading(); wx.showToast({ title: '网络错误', icon: 'none' }); reject() }
    })
  })
}
module.exports = { req: req, get: function(u,p) { return req({url:u,data:p}) }, post: function(u,d) { return req({url:u,method:'POST',data:d}) }, put: function(u,d) { return req({url:u,method:'PUT',data:d}) } }
