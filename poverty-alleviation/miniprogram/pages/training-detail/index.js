var rq = require('../../utils/request')
Page({
  data: { course: {}, myEnrolls: [], isEnrolled: false, loading: false, courseId: 0, startTimeText: '', endTimeText: '' },
  onLoad: function(e) { if (e.id) this.setData({ courseId: Number(e.id) }); this.loadData() },
  loadData: function() {
    var that = this, cid = that.data.courseId
    if (!cid) return
    rq.get('/training/course/page',{page:1,size:50}).then(function(r){
      var list=r.data?.records||[],c=list.find(function(c){return c.courseId===cid})
      if(c) that.setData({course:c,startTimeText:c.startTime?c.startTime.substring(0,10):'-',endTimeText:c.endTime?c.endTime.substring(0,10):'-'})
    }).catch(function(){})
    rq.get('/training/enrollment/page',{courseId:cid}).then(function(r){
      var list=r.data?.records||[], mapped=list.map(function(i){i.enrollTimeText=i.enrollTime?i.enrollTime.substring(0,10):'';return i})
      that.setData({myEnrolls:mapped,isEnrolled:mapped.length>0})
    }).catch(function(){})
  },
  statusText: function(s) { return ['','待审核','已通过','已签到','已拒绝'][s]||'未知' },
  handleEnroll: function() {
    var that=this; that.setData({loading:true})
    rq.post('/training/enrollment',{courseId:that.data.courseId}).then(function(){wx.showToast({title:'报名成功',icon:'success'});that.loadData()}).catch(function(){}).finally(function(){that.setData({loading:false})})
  }
})
