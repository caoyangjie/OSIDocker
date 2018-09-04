<html>
<head>
  <meta charset="UTF-8"></meta>
  <meta name="viewport" content="width=device-width, initial-scale=1"></meta>
  <meta name="description" content=""></meta>

  <style>
	* {
		-webkit-box-sizing: border-box;-moz-box-sizing: border-box;
		font-family: "SimSun","Microsoft Yahei","Lantinghei SC","Hiragino Sans GB","Helvetica Neue",Helvetica,Arial,sans-serif;
	}
  
    .container {
      display: flex;
      flex-direction: column;
      justify-content: center;
      text-align: center
    }
    
    .first {
      font-size: 16px;
      font-weight: bolder;
      margin-top: 2%
    }

    .second {
      margin-top: 1%
    }

    .three {
      display: flex;
      flex-direction: row;
      justify-content: center;
      text-align: center;
      margin-top: 5%
    }
    .t-one {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
    }
    .t-two {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      margin: 20px 0;
    }
    .t-three {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
    }
    .name {
      font-weight: bold;
      font-size: 14px
    }
    .dis {
      font-size: 12px;
    }
    .two-box {
      width: 90%;
      padding: 20px;
      box-shadow: 0px 0px 20px #c5c5c5;
      border-radius: 10px;
      border: 1px solid rgb(82, 82, 82)    
	  }
    .four {
      display: flex;
      flex-direction: row;
      justify-content: center;
      text-align: center;
      margin-top: 5%
    }
    .four-two {
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: 94%
    }
    .four-two-one {
      display: flex;
      flex-direction: row;
      align-items: center
    }
    .four-two-two {
      width: 100%;
      margin-top: 2%
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="first">
      ${bankName}账户历史交易明细
    </div>
    <div class="second">
      ${startDate}-${endDate}
    </div>
    <div style="text-align: center">
      <img src="./pc.jpg" style="width: 660px; height: auto">
    </div>
    <div class="three">
      <div class="one-box"></div>
      <div class="two-box">
        <div class="t-one">
          <span style="text-align: left">
            <span class="name">户名:</span>
            <span>${userName}</span>
          </span>
          <span style="text-align: right">
            <span class="name">用户行:</span>
            <span>个人活期结算户</span>
          </span>
        </div>
        <div class="t-two">
          <span style="text-align: left">
            <span class="name">账号:</span>
            <span>${accountNo}</span>
          </span>
          <span style="text-align: right">
            <span class="name">币种:</span>
            <span>人民币</span>
          </span>
        </div>
      </div>
      <div class="one-box"></div>
    </div>
    <div class="four">
      <div class="four-one"></div>
      <div class="four-two">
        <div class="four-two-one">
          <div style="height: 10px; width: 10px; background: #000; margin-right: 10px"></div>
          <div class="name">交易记录（最近${counts}条）</div>
        </div>
        <div class="four-two-two">
          <table style="width: 100%; border: 1px solid rgb(82, 82, 82)">
            <thead style="font-weight:bold">
              <tr>
                <td>时间</td>
                <td>交易类型</td>
                <td>摘要</td>
                <td>交易金额</td>
                <td>余额</td>
                <td>对方账户</td>
                <td>对方账户名</td>
                <td>交易地点</td>
              </tr>
            </thead>
            <tbody>
            <#list 0..(transList!?size-1) as i>
            <tr>
                <td>${transList[i].transTimeStr!}</td>
                <td>${transList[i].transType!}</td>
                <td style="max-width: 150px">${transList[i].transRemark!}</td>
                <td>${transList[i].transMoneyStr!}</td>
                <td>${transList[i].balanceStr!}</td>
                <td>${transList[i].otherAccountStr!}</td>
                <td>${transList[i].otherAccountName!}</td>
                <td>${transList[i].transAddress!}</td>
            </tr>
            </#list>

            </tbody>
          </table>
        </div>
      </div>
      <div class="four-one"></div>
    </div>
  </div>
</body>
</html>