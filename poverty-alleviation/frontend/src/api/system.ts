import request from '@/utils/request'

// ======== 系统认证 ========
export interface LoginData {
  username: string
  password: string
  captcha?: string
  captchaKey?: string
}
export interface UserData {
  userId?: number
  username: string
  password?: string
  realName: string
  idCard?: string
  phone?: string
  roleCode: string
  status?: number
}

export function login(data: LoginData) { return request.post('/system/auth/login', data) }
export function logout() { return request.post('/system/auth/logout') }

// ======== 用户管理 ========
export function getUserPage(params: any) { return request.get('/system/user/page', { params }) }
export function getUserById(id: number) { return request.get(`/system/user/${id}`) }
export function createUser(data: UserData) { return request.post('/system/user', data) }
export function updateUser(id: number, data: UserData) { return request.put(`/system/user/${id}`, data) }
export function deleteUser(id: number) { return request.delete(`/system/user/${id}`) }
export function toggleUserStatus(id: number, status: number) { return request.put(`/system/user/${id}/status?status=${status}`) }
export function resetPassword(id: number) { return request.put(`/system/user/${id}/reset-password`) }

// ======== 角色管理 ========
export function getRolePage(params: any) { return request.get('/system/role/page', { params }) }
export function getRoleList() { return request.get('/system/role/list') }
export function getRoleById(id: number) { return request.get(`/system/role/${id}`) }
export function createRole(data: any) { return request.post('/system/role', data) }
export function updateRole(id: number, data: any) { return request.put(`/system/role/${id}`, data) }
export function deleteRole(id: number) { return request.delete(`/system/role/${id}`) }

// ======== 权限管理 ========
export function getPermissionList() { return request.get('/system/permission/list') }
export function getPermissionTree() { return request.get('/system/permission/tree') }
export function createPermission(data: any) { return request.post('/system/permission', data) }
export function updatePermission(id: number, data: any) { return request.put(`/system/permission/${id}`, data) }
export function deletePermission(id: number) { return request.delete(`/system/permission/${id}`) }

// ======== 贫困户档案 ========
export function getFamilyPage(params: any) { return request.get('/poverty/family/page', { params }) }
export function getFamilyById(id: number) { return request.get(`/poverty/family/${id}`) }
export function createFamily(data: any) { return request.post('/poverty/family', data) }
export function updateFamily(id: number, data: any) { return request.put(`/poverty/family/${id}`, data) }
export function deleteFamily(id: number) { return request.delete(`/poverty/family/${id}`) }
export function getFamilyMembers(familyId: number) { return request.get(`/poverty/family/${familyId}/members`) }
export function createFamilyMember(familyId: number, data: any) { return request.post(`/poverty/family/${familyId}/member`, data) }
export function deleteFamilyMember(memberId: number) { return request.delete(`/poverty/family/member/${memberId}`) }
export function updateFamilyMember(memberId: number, data: any) { return request.put(`/poverty/family/member/${memberId}`, data) }

// ======== 帮扶结对 ========
export function getPairingPage(params: any) { return request.get('/assistance/pairing/page', { params }) }
export function getPairingById(id: number) { return request.get(`/assistance/pairing/${id}`) }
export function createPairing(data: any) { return request.post('/assistance/pairing', data) }
export function updatePairing(id: number, data: any) { return request.put(`/assistance/pairing/${id}`, data) }
export function deletePairing(id: number) { return request.delete(`/assistance/pairing/${id}`) }

// ======== 帮扶措施 ========
export function getMeasurePage(params: any) { return request.get('/assistance/measure/page', { params }) }
export function createMeasure(data: any) { return request.post('/assistance/measure', data) }
export function updateMeasure(id: number, data: any) { return request.put(`/assistance/measure/${id}`, data) }
export function deleteMeasure(id: number) { return request.delete(`/assistance/measure/${id}`) }

// ======== 走访记录 ========
export function getVisitPage(params: any) { return request.get('/visit/record/page', { params }) }
export function getVisitById(id: number) { return request.get(`/visit/record/${id}`) }
export function createVisit(data: any) { return request.post('/visit/record', data) }
export function deleteVisit(id: number) { return request.delete(`/visit/record/${id}`) }

// ======== 项目管理 ========
export function getProjectPage(params: any) { return request.get('/project/page', { params }) }
export function getProjectById(id: number) { return request.get(`/project/${id}`) }
export function createProject(data: any) { return request.post('/project', data) }
export function updateProject(id: number, data: any) { return request.put(`/project/${id}`, data) }
export function deleteProject(id: number) { return request.delete(`/project/${id}`) }
export function auditProject(id: number, data: any) { return request.post(`/project/${id}/audit`, data) }
export function getProjectAuditLogs(id: number) { return request.get(`/project/${id}/audit-logs`) }

// ======== 困难需求 ========
export function getNeedPage(params: any) { return request.get('/need/page', { params }) }
export function createNeed(data: any) { return request.post('/need', data) }
export function updateNeed(id: number, data: any) { return request.put(`/need/${id}`, data) }
export function deleteNeed(id: number) { return request.delete(`/need/${id}`) }
export function reviewNeed(id: number, params: any) { return request.put(`/need/${id}/review`, null, { params }) }

// ======== 资金捐赠 ========
export function getDonationMoneyPage(params: any) { return request.get('/donation/money/page', { params }) }
export function createDonationMoney(data: any) { return request.post('/donation/money', data) }
export function updateDonationMoney(id: number, data: any) { return request.put(`/donation/money/${id}`, data) }
export function deleteDonationMoney(id: number) { return request.delete(`/donation/money/${id}`) }

// ======== 物资捐赠 ========
export function getDonationGoodsPage(params: any) { return request.get('/donation/goods/page', { params }) }
export function createDonationGoods(data: any) { return request.post('/donation/goods', data) }
export function updateDonationGoods(id: number, data: any) { return request.put(`/donation/goods/${id}`, data) }
export function deleteDonationGoods(id: number) { return request.delete(`/donation/goods/${id}`) }
export function distributeDonationGoods(id: number) { return request.put(`/donation/goods/${id}/distribute`) }

// ======== 就业岗位 ========
export function getJobPage(params: any) { return request.get('/job/position/page', { params }) }
export function createJob(data: any) { return request.post('/job/position', data) }
export function updateJob(id: number, data: any) { return request.put(`/job/position/${id}`, data) }
export function deleteJob(id: number) { return request.delete(`/job/position/${id}`) }

// ======== 岗位申请 ========
export function getJobApplicationPage(params: any) { return request.get('/job/application/page', { params }) }
export function createJobApplication(data: any) { return request.post('/job/application', data) }
export function updateJobApplicationStatus(id: number, status: number) { return request.put(`/job/application/${id}/status?status=${status}`) }

// ======== 技能培训 ========
export function getTrainingCoursePage(params: any) { return request.get('/training/course/page', { params }) }
export function createTrainingCourse(data: any) { return request.post('/training/course', data) }
export function updateTrainingCourse(id: number, data: any) { return request.put(`/training/course/${id}`, data) }
export function deleteTrainingCourse(id: number) { return request.delete(`/training/course/${id}`) }

// ======== 培训报名 ========
export function getEnrollmentPage(params: any) { return request.get('/training/enrollment/page', { params }) }
export function createEnrollment(data: any) { return request.post('/training/enrollment', data) }
export function signInEnrollment(id: number) { return request.put(`/training/enrollment/${id}/sign-in`) }
export function reviewEnrollment(id: number, status: number) { return request.put(`/training/enrollment/${id}/review?status=${status}`) }

// ======== 志愿活动 ========
export function getActivityPage(params: any) { return request.get('/volunteer/activity/page', { params }) }
export function createActivity(data: any) { return request.post('/volunteer/activity', data) }
export function updateActivity(id: number, data: any) { return request.put(`/volunteer/activity/${id}`, data) }
export function deleteActivity(id: number) { return request.delete(`/volunteer/activity/${id}`) }

// ======== 志愿服务记录 ========
export function getVolunteerRecordPage(params: any) { return request.get('/volunteer/record/page', { params }) }
export function createVolunteerRecord(data: any) { return request.post('/volunteer/record', data) }
export function signInVolunteer(id: number) { return request.put(`/volunteer/record/${id}/sign-in`) }
export function signOutVolunteer(id: number) { return request.put(`/volunteer/record/${id}/sign-out`) }
export function submitVolunteerFeedback(id: number, params: any) { return request.put(`/volunteer/record/${id}/feedback`, null, { params }) }
export function getActivityById(id: number) { return request.get(`/volunteer/activity/${id}`) }

// ======== 志愿者积分 ========
export function getMyScore() { return request.get('/volunteer/score/my') }
export function getScorePage(params: any) { return request.get('/volunteer/score/page', { params }) }
export function getScoreRanking(params?: any) { return request.get('/volunteer/score/ranking', { params }) }

// ======== 干部工作台 ========
export function getCadreDashboard() { return request.get('/cadre/dashboard') }
export function getCadreFamilies() { return request.get('/cadre/families') }
export function getCadreFamilyOverview(familyId: number) { return request.get(`/cadre/family/${familyId}/overview`) }
export function getCadreFamilyTimeline(familyId: number) { return request.get(`/cadre/family/${familyId}/timeline`) }
export function getCadreTasks() { return request.get('/cadre/tasks') }

// ======== 数据大屏 ========
export function getBigScreenOverview() { return request.get('/dashboard/big-screen/overview') }
export function aiChat(message: string) { return request.post('/ai/chat', { message }) }

// ======== 防返贫预警 ========
export function getRiskAlerts() { return request.get('/risk/alerts') }
export function getRiskAlertPage(params: any) { return request.get('/risk/alert/page', { params }) }
export function getRiskAssessPage(params: any) { return request.get('/risk/assess/page', { params }) }
export function getRiskStats() { return request.get('/risk/stats') }
export function assessFamily(familyId: number) { return request.post(`/risk/assess/${familyId}`) }
export function handleRiskAlert(id: number) { return request.put(`/risk/alert/${id}/handle`) }

// ======== 智能匹配 ========
export function getRiskMatches(familyId?: number) { return request.get('/risk/matches', { params: { familyId } }) }
export function generateMatches(familyId: number) { return request.post(`/risk/matches/generate/${familyId}`) }
export function acceptMatch(id: number) { return request.put(`/risk/matches/${id}/accept`) }

// ======== 志愿者结对 ========
export function getMyPairings() { return request.get('/volunteer/pairing/my') }
export function getPairingFamilyDashboard(familyId: number) { return request.get(`/volunteer/pairing/family/${familyId}/dashboard`) }
export function getVolunteerTasks() { return request.get('/volunteer/tasks') }
export function getVolunteerImpact() { return request.get('/volunteer/impact') }
export function getUnreadCount() { return request.get('/volunteer/notifications/unread-count') }
export function getNotifications(params?: any) { return request.get('/volunteer/notifications', { params }) }
export function markNotificationRead(id: number) { return request.put(`/volunteer/notifications/${id}/read`) }
export function markAllNotificationsRead() { return request.put('/volunteer/notifications/read-all') }

// ======== 志愿者证书 ========
export function getCertificatePage(params: any) { return request.get('/volunteer/certificate/page', { params }) }

// ======== 志愿者走访 ========
export function createVolunteerVisit(data: any) { return request.post('/visit/record/volunteer', data) }
export function getVolunteerVisitPage(params: any) { return request.get('/visit/record/volunteer/page', { params }) }

// ======== 信息公开 ========
export function getNewsPage(params: any) { return request.get('/news/article/page', { params }) }
export function getNewsById(id: number) { return request.get(`/news/article/${id}`) }
export function createNews(data: any) { return request.post('/news/article', data) }
export function updateNews(id: number, data: any) { return request.put(`/news/article/${id}`, data) }
export function deleteNews(id: number) { return request.delete(`/news/article/${id}`) }

// ======== 数据字典 ========
export function getDictByType(dictType: string) { return request.get('/news/dict/type', { params: { dictType } }) }
export function createDict(data: any) { return request.post('/news/dict', data) }
export function updateDict(id: number, data: any) { return request.put(`/news/dict/${id}`, data) }
export function deleteDict(id: number) { return request.delete(`/news/dict/${id}`) }

// ======== 统计看板 ========
export function getStatistics() { return request.get('/dashboard/statistics') }
