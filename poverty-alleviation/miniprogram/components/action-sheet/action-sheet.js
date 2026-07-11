Component({
  properties: { visible: { type: Boolean, value: false }, title: { type: String, value: '' }, items: { type: Array, value: [] } },
  methods: {
    onItemTap: function(e) { var idx = e.currentTarget.dataset.index; this.triggerEvent('select', { index: idx, item: this.data.items[idx] }); this.setData({ visible: false }); },
    onClose: function() { this.setData({ visible: false }); this.triggerEvent('close'); }
  }
})
