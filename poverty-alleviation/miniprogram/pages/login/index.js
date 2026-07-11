var app = getApp()
Page({
  data: { username: '', password: '', loading: false },
  onLoad: function() {},
  handleLogin: function() {
    var that = this, u = that.data.username, p = that.data.password
    if (!u || !p) { wx.showToast({ title: '请输入账号和密码', icon: 'none' }); return }
    that.setData({ loading: true })
    wx.request({
      url: app.globalData.baseUrl + '/system/auth/login', method: 'POST',
      data: { username: u, password: p },
      header: { 'Content-Type': 'application/json' },
      success: function(res) {
        if (res.data && res.data.code === 200) {
          wx.setStorageSync('token', res.data.data.token)
          wx.setStorageSync('userInfo', res.data.data.userInfo)
          wx.showToast({ title: '登录成功', icon: 'success' })
          wx.reLaunch({ url: '/pages/home/index' })
        } else { wx.showToast({ title: res.data?.message || '登录失败', icon: 'none' }) }
      },
      fail: function() { wx.showToast({ title: '登录失败', icon: 'none' }) },
      complete: function() { that.setData({ loading: false }) }
    })
  }
})
