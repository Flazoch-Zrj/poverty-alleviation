var rq = require('../../utils/request')
Page({
  data: { form: { householderName: '', address: '', familySize: 1, annualIncome: 0 }, loading: false },
  onShow: function() {
    var that = this, fid = wx.getStorageSync('familyId') || ''
    if (!fid) return
    rq.get('/poverty/family/' + fid).then(function(r) {
      var f = r.data?.family || r.data
      that.setData({ form: { householderName: f?.householderName || '', address: f?.address || '', familySize: f?.familySize || 1, annualIncome: f?.annualIncome || 0 } })
    }).catch(function(){})
  },
  handleSubmit: function() {
    var that = this, fid = wx.getStorageSync('familyId') || ''
    if (!fid) return
    that.setData({ loading: true })
    rq.put('/poverty/family/' + fid, that.data.form).then(function() { wx.showToast({ title:'保存成功', icon:'success' }); setTimeout(function(){wx.navigateBack()},1500) }).catch(function(){}).finally(function(){ that.setData({loading:false}) })
  }
})
