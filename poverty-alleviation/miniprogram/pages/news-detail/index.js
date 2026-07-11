var rq = require('../../utils/request')
Page({
  data: { article: {} },
  onLoad: function(e) { var that=this; if (e.id) rq.get('/news/article/'+e.id).then(function(r){var a=r.data||{};a.createDate=a.createTime?a.createTime.substring(0,10):'';that.setData({article:a})}).catch(function(){}) }
})
