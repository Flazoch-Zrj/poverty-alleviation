var rq = require('../../utils/request')
Page({
  data: { members: [] },
  onShow: function() {
    var that = this, fid = wx.getStorageSync('familyId') || ''
    if (!fid) return
    rq.get('/poverty/family/' + fid).then(function(r) { that.setData({ members: r.data?.members || [] }) }).catch(function(){})
  },
  goBack: function() { wx.navigateBack() }
})
