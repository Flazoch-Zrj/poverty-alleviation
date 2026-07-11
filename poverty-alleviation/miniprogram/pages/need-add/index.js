var rq = require('../../utils/request')
Page({
  data: { types: ['医疗','教育','资金','物资','技术','其他'], loading: false, form: { typeIndex: 0, title: '', description: '', amount: '' } },
  onTypeChange: function(e) { this.setData({ 'form.typeIndex': e.detail.value }) },
  handleSubmit: function() {
    var that = this, f = that.data.form
    if (!f.title) { wx.showToast({ title:'请输入标题', icon:'none' }); return }
    that.setData({ loading: true })
    rq.post('/need', { needType: that.data.types[f.typeIndex], title: f.title, description: f.description, targetAmount: Number(f.amount) || 0 })
      .then(function() { wx.showToast({ title:'发布成功', icon:'success' }); setTimeout(function(){wx.navigateBack()},1500) })
      .catch(function(){})
      .finally(function(){ that.setData({loading:false}) })
  }
})
