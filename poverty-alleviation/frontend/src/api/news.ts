/**
 * 新闻资讯 API 模块
 * 支持 mock 模式和真实 API 模式，通过 __USE_MOCK__ 开关切换
 * 
 * 使用方式:
 *   import { getNewsPage, getNewsById } from '@/api/news'
 *   const res = await getNewsPage({ page: 1, size: 10, type: '1' })
 *   // res.data.records, res.data.total
 */

import request from '@/utils/request'

// ============================================================
// 开关：true = 使用模拟数据，false = 调用真实后端 API
// ============================================================
const __USE_MOCK__ = true

// ============================================================
// 类型定义
// ============================================================
export interface NewsItem {
  id: number
  title: string
  type: string       // '1'=政策新闻 '2'=脱贫案例 '3'=通知公告 '4'=捐赠公示 '5'=中央文件
  content: string
  createTime: string
  viewCount: number
  fileUrl?: string
  fileName?: string
  fileSize?: string
  source?: string
  summary?: string
}

export interface NewsPageParams {
  page?: number
  size?: number
  type?: string
  keyword?: string
}

export interface NewsPageResult {
  records: NewsItem[]
  total: number
}

// ============================================================
// Mock 数据
// ============================================================
const mockNews: NewsItem[] = [
  {
    id: 1,
    title: '国务院扶贫办：2024年防止返贫监测帮扶集中排查工作正式启动',
    type: '5',
    content: `<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">各省、自治区、直辖市乡村振兴局（扶贫办），新疆生产建设兵团乡村振兴局：</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">为贯彻落实党中央、国务院关于巩固拓展脱贫攻坚成果同乡村振兴有效衔接的决策部署，切实做好防止返贫监测帮扶工作，现就开展2024年防止返贫监测帮扶集中排查工作通知如下：</p>
<h3 style="font-size:18px;font-weight:700;margin:24px 0 12px;color:#0A2647;">一、总体要求</h3>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">以习近平新时代中国特色社会主义思想为指导，全面贯彻落实党的二十大精神，坚持把防止返贫摆在突出位置，健全防止返贫监测和帮扶机制，对易返贫致贫人口实施常态化监测，重点监测收入水平变化和"两不愁三保障"巩固情况，做到早发现、早干预、早帮扶，坚决守住不发生规模性返贫的底线。</p>
<h3 style="font-size:18px;font-weight:700;margin:24px 0 12px;color:#0A2647;">二、排查范围</h3>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">本次集中排查覆盖全国所有行政村，重点关注以下三类对象：一是脱贫不稳定户，二是边缘易致贫户，三是因病因灾因意外事故等刚性支出较大或收入大幅缩减导致基本生活出现严重困难户。</p>
<figure style="margin:24px 0;text-align:center;">
  <img src="/images/特色农产品丰收.jpg" alt="乡村振兴" style="max-width:100%;border-radius:12px;box-shadow:0 4px 16px rgba(0,0,0,0.08);" />
  <figcaption style="margin-top:8px;font-size:13px;color:#9CA3AF;">乡村振兴工作推进会议</figcaption>
</figure>
<h3 style="font-size:18px;font-weight:700;margin:24px 0 12px;color:#0A2647;">三、工作要求</h3>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">各地要高度重视，加强组织领导，确保排查工作取得实效。要充分利用全国防返贫监测信息系统，加强数据共享比对，提高监测预警的及时性和准确性。同时要强化部门协作，落实好教育、医疗、住房、就业等各项帮扶政策。</p>
<blockquote style="margin:20px 0;padding:16px 20px;border-left:3px solid #2D7A9B;background:#F8FAFC;border-radius:0 8px 8px 0;color:#6B7280;font-style:italic;">
  各级乡村振兴部门要以"时时放心不下"的责任感，扎实做好防止返贫监测帮扶工作，让脱贫基础更加稳固、成效更可持续。
</blockquote>`,
    createTime: '2024-03-15 09:30:00',
    viewCount: 1286,
    fileUrl: '/pdfs/关于开展2024年防止返贫监测帮扶集中排查工作的通知.pdf',
    fileName: '关于开展2024年防止返贫监测帮扶集中排查工作的通知.pdf',
    fileSize: '2.3 MB',
    source: '国务院扶贫办',
    summary: '为贯彻落实党中央、国务院决策部署，切实做好防止返贫监测帮扶工作，现就开展2024年集中排查工作通知如下...',
  },
  {
    id: 2,
    title: '2024年一季度全国脱贫人口务工就业规模达3286万人',
    type: '1',
    content: `<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">记者从国家乡村振兴局获悉，截至2024年3月底，全国脱贫人口务工就业规模达到3286万人，超过去年同期水平，实现了脱贫人口就业规模的稳定增长。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">今年以来，各地区各部门把促进脱贫人口就业作为巩固拓展脱贫攻坚成果的重要举措，通过发展产业吸纳就业、加强劳务协作输出就业、支持返乡创业带动就业、开发公益性岗位安置就业等多种渠道，全力稳住脱贫人口就业基本盘。</p>
<figure style="margin:24px 0;text-align:center;">
  <img src="/images/培训班上课现场.png" alt="技能培训现场" style="max-width:100%;border-radius:12px;box-shadow:0 4px 16px rgba(0,0,0,0.08);" />
  <figcaption style="margin-top:8px;font-size:13px;color:#9CA3AF;">脱贫人口技能培训现场</figcaption>
</figure>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">在产业吸纳方面，各地积极发展乡村特色产业、农产品加工业和农村电商，为脱贫人口创造了大量就近就业岗位。据统计，一季度全国乡村特色产业新增就业岗位超过120万个，其中脱贫人口占比超过30%。在劳务协作方面，东西部省份深化劳务协作机制，广东、浙江、福建等用工大省与中西部劳动力输出省份签订劳务协作协议，通过"点对点"专车专列输送脱贫人口返岗务工，一季度累计输送超过80万人。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">在返乡创业方面，各地加大政策扶持力度，通过创业担保贷款、创业补贴、税收优惠等措施，鼓励支持脱贫人口返乡创业。一季度新增脱贫人口返乡创业主体2.3万个，带动就业9.6万人。在公益性岗位方面，各地统筹用好乡村保洁员、护林员、护路员等公益性岗位，优先安排脱贫人口就业，一季度累计安置脱贫人口286万人。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">下一步，国家乡村振兴局将继续加大脱贫人口就业帮扶力度，强化职业技能培训，拓宽就业渠道，确保脱贫人口就业规模保持稳定。</p>`,
    createTime: '2024-04-20 14:00:00',
    viewCount: 856,
    source: '国家乡村振兴局',
    summary: '截至2024年3月底，全国脱贫人口务工就业规模达到3286万人，超过去年同期水平。',
  },
  {
    id: 3,
    title: '湖南湘西：特色产业铺就乡村振兴"致富路"',
    type: '2',
    content: `<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">近年来，湖南省湘西土家族苗族自治州依托当地资源禀赋，大力发展特色种植产业，走出了一条"产业扶贫、产业富民"的乡村振兴之路。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">在花垣县十八洞村，猕猴桃种植已成为村民增收的"金果子"。村里成立了专业合作社，采取"公司+合作社+农户"模式，统一供苗、统一技术、统一销售，带动126户村民发展猕猴桃种植，户均年增收超过5000元。</p>
<figure style="margin:24px 0;text-align:center;">
  <img src="/images/农技专家田间培训.jpg" alt="农技专家田间指导" style="max-width:100%;border-radius:12px;box-shadow:0 4px 16px rgba(0,0,0,0.08);" />
  <figcaption style="margin-top:8px;font-size:13px;color:#9CA3AF;">农技专家在田间地头指导种植技术</figcaption>
</figure>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">除了猕猴桃，湘西州还因地制宜发展了茶叶、油茶、中药材、特色养殖等多元产业。在保靖县黄金村，万亩黄金茶种植基地已成为当地农民的"绿色银行"。村里通过土地流转、务工就业、茶叶销售等多种方式带动农户增收，茶叶年产值超过8000万元，带动全村人均可支配收入从2019年的8200元增长到2023年的21800元。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">"以前种玉米水稻，一年到头挣不了几个钱。现在改种猕猴桃，技术有人教，销路不用愁，日子越过越红火。"村民龙大爷乐呵呵地说。如今，龙大爷家的5亩猕猴桃园年收入超过6万元，去年他还把在外打工的儿子叫回来一起干，父子俩计划再扩种3亩。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">湘西州的产业扶贫经验正在全国推广。据统计，截至2023年底，全国脱贫地区特色产业产值达到1.2万亿元，带动超过3000万脱贫人口增收致富。乡村振兴，产业先行。湘西的实践再次证明，找准路子、用对法子，绿水青山就能变成金山银山。</p>`,
    createTime: '2024-03-28 10:30:00',
    viewCount: 623,
    source: '湖南省乡村振兴局',
    summary: '湘西州依托当地资源大力发展特色种植产业，走出产业扶贫、产业富民之路。',
  },
  {
    id: 4,
    title: '关于做好2024年清明节假期值班工作的通知',
    type: '3',
    content: `<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">各乡镇（街道）乡村振兴办、局机关各科室：</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">根据上级统一安排，2024年清明节假期为4月4日至6日，共3天。为做好假期值班工作，确保节日期间各项工作正常运转，现将有关事项通知如下：</p>
<h3 style="font-size:18px;font-weight:700;margin:24px 0 12px;color:#0A2647;">一、严格落实值班制度</h3>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">各单位要严格执行24小时值班和领导带班制度，确保通讯畅通。值班人员要坚守岗位，认真履行职责，做好值班记录和交接班工作。如遇重要紧急情况，要第一时间向带班领导报告，并采取有效措施妥善处置。严禁值班期间脱岗、漏岗、擅离职守。假期期间，局机关将不定期对各单位值班情况进行抽查，对值班制度落实不到位的将予以通报。</p>
<h3 style="font-size:18px;font-weight:700;margin:24px 0 12px;color:#0A2647;">二、做好安全防范工作</h3>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">节前要对办公区域进行一次全面安全检查，重点排查用电设备、消防设施、门窗锁具等安全隐患，确保电源关闭、门窗锁好。要加强对档案室、财务室、机房等重点部位的安全管理，防范各类安全事故发生。同时，各单位要加强对干部职工的安全教育，提醒注意出行交通安全和防火防盗，确保度过一个平安祥和的假期。</p>
<h3 style="font-size:18px;font-weight:700;margin:24px 0 12px;color:#0A2647;">三、倡导文明祭扫</h3>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">清明节是中华民族缅怀先人、寄托哀思的传统节日。广大干部职工要带头践行文明祭扫理念，提倡通过敬献鲜花、植树绿化、网络祭扫等低碳环保方式缅怀故人，自觉抵制焚烧纸钱、燃放鞭炮等陈规陋习，严防森林火灾，共同维护优美整洁的城乡环境。</p>`,
    createTime: '2024-03-30 16:00:00',
    viewCount: 342,
    source: '县乡村振兴局办公室',
    summary: '2024年清明节假期值班工作安排通知。',
  },
  {
    id: 5,
    title: '2024年第一季度社会捐赠资金使用情况公示',
    type: '4',
    content: `<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">根据《中华人民共和国慈善法》和《社会捐赠资金管理办法》有关规定，现将2024年第一季度社会捐赠资金接收和使用情况公示如下：</p>
<table style="width:100%;border-collapse:collapse;margin:20px 0;">
  <thead>
    <tr style="background:#F9FAFB;">
      <th style="padding:10px 14px;border:1px solid #E5E7EB;text-align:left;">项目</th>
      <th style="padding:10px 14px;border:1px solid #E5E7EB;text-align:right;">金额（元）</th>
    </tr>
  </thead>
  <tbody>
    <tr><td style="padding:10px 14px;border:1px solid #E5E7EB;">一、上期结余</td><td style="padding:10px 14px;border:1px solid #E5E7EB;text-align:right;">1,285,630.00</td></tr>
    <tr><td style="padding:10px 14px;border:1px solid #E5E7EB;">二、本期收入</td><td style="padding:10px 14px;border:1px solid #E5E7EB;text-align:right;">586,200.00</td></tr>
    <tr><td style="padding:10px 14px;border:1px solid #E5E7EB;">　其中：企业捐赠</td><td style="padding:10px 14px;border:1px solid #E5E7EB;text-align:right;">420,000.00</td></tr>
    <tr><td style="padding:10px 14px;border:1px solid #E5E7EB;">　个人捐赠</td><td style="padding:10px 14px;border:1px solid #E5E7EB;text-align:right;">166,200.00</td></tr>
    <tr><td style="padding:10px 14px;border:1px solid #E5E7EB;font-weight:600;">三、本期支出</td><td style="padding:10px 14px;border:1px solid #E5E7EB;text-align:right;">420,000.00</td></tr>
    <tr><td style="padding:10px 14px;border:1px solid #E5E7EB;">　其中：助学助困</td><td style="padding:10px 14px;border:1px solid #E5E7EB;text-align:right;">180,000.00</td></tr>
    <tr><td style="padding:10px 14px;border:1px solid #E5E7EB;">　医疗救助</td><td style="padding:10px 14px;border:1px solid #E5E7EB;text-align:right;">240,000.00</td></tr>
    <tr><td style="padding:10px 14px;border:1px solid #E5E7EB;font-weight:600;">四、期末结余</td><td style="padding:10px 14px;border:1px solid #E5E7EB;text-align:right;">1,451,830.00</td></tr>
  </tbody>
</table>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">以上资金使用情况接受社会各界监督，如有疑问请拨打监督电话：0718-1234567。</p>`,
    createTime: '2024-04-01 09:00:00',
    viewCount: 215,
    source: '县慈善总会',
    summary: '2024年第一季度社会捐赠资金接收和使用情况公示。',
  },
  {
    id: 6,
    title: '贵州黔东南：东西部协作赋能乡村振兴',
    type: '1',
    content: `<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">近年来，贵州省黔东南苗族侗族自治州紧紧抓住东西部协作机遇，与广东省佛山市深化产业合作、劳务协作、人才交流，为乡村振兴注入了强劲动力。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">据统计，2023年以来，佛山市累计投入协作资金3.2亿元，实施产业项目42个，帮助黔东南州农村劳动力转移就业1.8万人。在产业合作方面，两地共建了6个产业园区，引进佛山企业28家，涵盖电子信息、纺织服装、农产品加工等领域，带动当地就业5600余人。</p>
<figure style="margin:24px 0;text-align:center;">
  <img src="/images/办理贷款手续.png" alt="东西部协作" style="max-width:100%;border-radius:12px;box-shadow:0 4px 16px rgba(0,0,0,0.08);" />
  <figcaption style="margin-top:8px;font-size:13px;color:#9CA3AF;">东西部协作项目签约仪式</figcaption>
</figure>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">在劳务协作方面，两地建立了"佛山企业+黔东南基地"的劳务合作模式，通过定向招聘、订单培训等方式，累计向佛山输送务工人员1.8万人，其中脱贫人口超过6000人。务工人员月平均工资达到4800元以上，比外出前增加了近一倍。佛山市还为黔东南州务工人员提供了住房补贴、子女入学等配套服务，让他们"输得出、稳得住、能致富"。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">在人才交流方面，两地互派挂职干部86人次，佛山市选派32名教育、医疗专家到黔东南州开展帮扶工作，帮助当地培训专业技术人才1200余人次。通过佛山专家"传帮带"，黔东南州的医疗水平显著提升，多项新技术实现了"零的突破"。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">东西部协作不仅带来了资金和项目，更带来了先进的理念和技术。黔东南州乡村振兴局负责人表示，将进一步加强与佛山市的对接合作，把东西部协作的成果巩固好、拓展好，让协作之花在苗乡侗寨结出更加丰硕的果实。</p>`,
    createTime: '2024-04-10 11:20:00',
    viewCount: 534,
    source: '黔东南州乡村振兴局',
    summary: '贵州省黔东南州深化东西部协作，与佛山市合作为乡村振兴注入强劲动力。',
  },
  {
    id: 7,
    title: '防止返贫监测对象申报政策"明白纸"',
    type: '3',
    content: `<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">广大农民朋友：</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">为让广大农民群众了解防止返贫监测对象申报相关政策，现将有关事项告知如下：</p>
<h3 style="font-size:18px;font-weight:700;margin:24px 0 12px;color:#0A2647;">一、申报条件</h3>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">家中因大病、重残、自然灾害、意外事故等导致基本生活出现严重困难，且家庭收入低于当年防止返贫监测范围的农户，均可申报防止返贫监测对象。具体标准为：以家庭为单位，年人均纯收入低于当年度防止返贫监测范围（2024年为年人均纯收入低于7500元），且存在返贫致贫风险的农户。</p>
<h3 style="font-size:18px;font-weight:700;margin:24px 0 12px;color:#0A2647;">二、申报方式</h3>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">可通过以下三种方式申报：
<br/>1. <strong>向村级组织提出申请</strong>：携带本人身份证和户口簿到所在村委会填写《防止返贫监测对象申报表》，由村委会初审后报乡镇审核；
<br/>2. <strong>拨打12317防止返贫监测电话</strong>：全国统一的防止返贫监测电话12317，工作日工作时间有专人接听，反映相关情况；
<br/>3. <strong>使用全国防返贫监测信息系统微信小程序</strong>：通过微信搜索"全国防返贫监测"小程序，在线填写申报信息并上传相关证明材料。</p>
<h3 style="font-size:18px;font-weight:700;margin:24px 0 12px;color:#0A2647;">三、认定流程</h3>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">申报受理后，乡镇将在10个工作日内组织人员入户核查，按照"村级评议、乡镇审核、县级审定"的程序进行认定。经认定为监测对象的，将安排帮扶联系人，根据致贫返贫原因和帮扶需求，从产业帮扶、就业帮扶、健康帮扶、教育帮扶、住房安全保障、饮水安全保障、综合保障等方面落实针对性帮扶措施。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">防止返贫监测帮扶工作是巩固拓展脱贫攻坚成果的重要举措。希望广大农民朋友积极了解政策，符合条件的主动申报。如有疑问，可拨打当地乡村振兴部门咨询电话或到村委会咨询。</p>`,
    createTime: '2024-02-20 08:30:00',
    viewCount: 1890,
    source: '县乡村振兴局',
    summary: '防止返贫监测对象申报条件、方式及政策告知。',
  },
  {
    id: 8,
    title: '江西赣州：昔日贫困村变身"网红"民宿村',
    type: '2',
    content: `<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">走进江西省赣州市于都县梓山镇潭头村，映入眼帘的是整洁的村道、别致的民宿、笑容满面的村民。这个曾经的深度贫困村，如今已成为远近闻名的"网红"民宿村。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">"以前村里穷，年轻人都外出打工。现在村里搞起了乡村旅游，我家老房子改造成民宿，一年能挣七八万。"村民孙大哥高兴地说。孙大哥家的民宿有6间客房，旺季时一房难求，去年全年收入超过8万元。在潭头村，像孙大哥这样经营民宿的农户已有26家，全村乡村旅游年收入突破500万元。</p>
<figure style="margin:24px 0;text-align:center;">
  <img src="/images/特色农产品丰收.jpg" alt="潭头村民宿" style="max-width:100%;border-radius:12px;box-shadow:0 4px 16px rgba(0,0,0,0.08);" />
  <figcaption style="margin-top:8px;font-size:13px;color:#9CA3AF;">潭头村特色民宿庭院</figcaption>
</figure>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">潭头村的蜕变是赣州市乡村振兴的一个缩影。近年来，赣州市依托丰富的红色资源和绿色生态，大力发展乡村旅游，带动群众增收致富。全市重点打造了18个乡村旅游精品示范点，形成了"红色研学+绿色康养+古色文化"的多元旅游产品体系。2023年，全市乡村旅游接待游客超过1200万人次，旅游综合收入突破80亿元，带动3.2万户脱贫户通过参与旅游产业实现增收。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">在潭头村的带动下，周边村庄也纷纷效仿。隔壁的窑前村利用传统制陶技艺，开发了陶艺体验项目，吸引游客前来体验手工制陶的乐趣；河背村则主打生态垂钓和农家菜，每到周末停车场一位难求。各村错位发展、优势互补，形成了"一村一品、串珠成链"的乡村旅游发展格局。</p>
<p style="font-size:16px;line-height:1.9;margin-bottom:20px;">"脱贫不是终点，而是新生活的起点。"潭头村党支部书记表示，下一步村里计划建设游客服务中心和农产品展销中心，进一步提升旅游接待能力，带动更多群众吃上"旅游饭"、走上"致富路"。</p>`,
    createTime: '2024-04-05 15:45:00',
    viewCount: 756,
    source: '赣州市乡村振兴局',
    summary: '江西省赣州市潭头村从深度贫困村变身网红民宿村，乡村旅游带动群众增收致富。',
  },
]

// ============================================================
// 模拟延迟（模拟网络请求）
// ============================================================
const delay = (ms: number = 400) => new Promise(resolve => setTimeout(resolve, ms))

// ============================================================
// 模拟 API 函数
// ============================================================
async function mockGetNewsPage(params: NewsPageParams): Promise<{ code: number; data: NewsPageResult; message: string }> {
  await delay()
  let filtered = [...mockNews]
  // 分类筛选
  if (params.type) {
    filtered = filtered.filter(n => n.type === params.type)
  }
  // 关键词搜索
  if (params.keyword) {
    const kw = params.keyword.toLowerCase()
    filtered = filtered.filter(n => n.title.includes(kw) || (n.summary || '').includes(kw))
  }
  // 总数
  const total = filtered.length
  // 分页
  const page = params.page || 1
  const size = params.size || 10
  const start = (page - 1) * size
  const records = filtered.slice(start, start + size)
  return { code: 200, data: { records, total }, message: 'success' }
}

async function mockGetNewsById(id: number): Promise<{ code: number; data: NewsItem | null; message: string }> {
  await delay(200)
  const item = mockNews.find(n => n.id === id) || null
  return { code: item ? 200 : 404, data: item, message: item ? 'success' : 'not found' }
}

// ============================================================
// 导出 API 函数
// ============================================================

/**
 * 获取新闻分页列表
 * @param params - 查询参数 { page, size, type, keyword }
 */
export async function getNewsPage(params: NewsPageParams = {}): Promise<{ code: number; data: NewsPageResult; message: string }> {
  if (__USE_MOCK__) {
    return mockGetNewsPage(params)
  }
  // 真实 API 调用（后端路径）
  return request.get('/news/article/page', { params })
}

/**
 * 根据 ID 获取新闻详情
 * @param id - 新闻 ID
 */
export async function getNewsById(id: number): Promise<{ code: number; data: NewsItem | null; message: string }> {
  if (__USE_MOCK__) {
    return mockGetNewsById(id)
  }
  return request.get(`/news/article/${id}`)
}
