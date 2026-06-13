exports.handler = async function (event, context) {
  // Obtain base domain protocol and host
  const rawUrl = event.rawUrl || `https://${event.headers.host || 'aura-portal.netlify.app'}${event.path}`;
  const url = new URL(rawUrl);
  
  // Extract parameters
  const params = event.queryStringParameters || {};
  const postId = params.postId || "1";
  
  // Base64 helper decoding
  const d64 = (str) => {
    if (!str) return null;
    try {
      let b64 = str.replace(/-/g, "+").replace(/_/g, "/");
      while (b64.length % 4) b64 += "=";
      return Buffer.from(b64, 'base64').toString('utf8');
    } catch (e) {
      return null;
    }
  };

  const author = d64(params.n) || "Aura Member";
  const desc = d64(params.d) || "Shared from Aura App.";
  let img = d64(params.i) || "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=1200";

  if (!img.startsWith("http")) {
    img = "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=1200";
  }

  // Detect bots
  const ua = (event.headers['user-agent'] || "").toLowerCase();
  const bots = ["whatsapp", "facebook", "twitter", "telegram", "slack", "discord", "googlebot"];
  const isBot = bots.some(bot => ua.includes(bot));

  if (isBot) {
    const title = `✨ ${author} shared a post on Aura! ✨`;
    const htmlOutput = `<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${title}</title>
  <meta name="description" content="${desc}">
  <meta property="og:title" content="${title}">
  <meta property="og:description" content="${desc}">
  <meta property="og:image" content="${img}">
  <meta property="og:image:width" content="1200">
  <meta property="og:image:height" content="630">
  <meta property="og:type" content="article">
  <meta property="og:site_name" content="Aura">
  <meta name="twitter:card" content="summary_large_image">
</head>
<body>
  <h1>${title}</h1>
  <p>${desc}</p>
</body>
</html>`;

    return {
      statusCode: 200,
      headers: { "Content-Type": "text/html;charset=UTF-8" },
      body: htmlOutput
    };
  }

  // Normal Mobile/Web Client View
  const title = `✨ ${author} shared a post!`;
  const appLink = `aura://post/${postId}`;
  const origin = `${url.protocol}//${url.host}`;
  const apkLink = `https://ais-pre-inawwf2545flos3colouiz-78211575748.asia-southeast1.run.app/APK_DOWNLOAD/app-debug.apk`;

  const htmlOutput = `<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1.0">
  <title>${title} | Aura</title>
  <meta property="og:title" content="${title}">
  <meta property="og:description" content="${desc}">
  <meta property="og:image" content="${img}">
  <meta property="og:type" content="article">
  <meta name="twitter:card" content="summary_large_image">
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;600;800&display=swap" rel="stylesheet">
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body {
      font-family: 'Plus Jakarta Sans', sans-serif;
      background: linear-gradient(135deg, #FAF5FF 0%, #F3E8FF 100%);
      display: flex; align-items: center; justify-content: center;
      min-height: 100vh; color: #111827; padding: 20px;
    }
    .card {
      background: white; border-radius: 24px;
      box-shadow: 0 15px 35px rgba(124, 77, 255, 0.08);
      max-width: 400px; width: 100%; text-align: center;
      overflow: hidden; border: 1.5px solid #F3E8FF;
    }
    .bar {
      height: 10px;
      background: linear-gradient(90deg,#7C4DFF,#D1C4E9,#FF4081);
    }
    .con { padding: 24px; }
    .brand {
      display: inline-flex; background: #F5F3FF;
      padding: 6px 14px; border-radius: 99px;
      color: #7C4DFF; font-size: 12px; font-weight: 800;
      margin-bottom: 16px;
    }
    .post-img {
      width: 100%; height: 180px; object-fit: cover;
      border-radius: 12px; margin-bottom: 20px;
    }
    h2 {
      font-size: 18px; font-weight: 800; color: #7C4DFF;
      line-height: 1.4; margin-bottom: 8px;
    }
    p.desc {
      color: #4B5563; font-size: 13px; line-height: 1.6;
      margin-bottom: 24px;
    }
    .btn {
      display: flex; align-items: center; justify-content: center;
      padding: 12px 20px; border-radius: 12px; font-weight: 600;
      font-size: 13px; text-decoration: none; margin-bottom: 10px;
      cursor: pointer;
    }
    .btn-p {
      background: #7C4DFF; color: white;
      box-shadow: 0 4px 10px rgba(124, 77, 255, 0.2);
    }
    .btn-s { background: #F3F4F6; color: #374151; }
    .loader {
      display: inline-block; width: 20px; height: 20px;
      border: 3px solid #F3E8FF; border-top-color: #7C4DFF;
      border-radius: 50%; animation: s 1s infinite linear;
      margin-bottom: 16px;
    }
    @keyframes s { 100% { transform: rotate(360deg); } }
    .foot { font-size: 10px; color: #9CA3AF; margin-top: 16px; }
  </style>
  <script>
    function openApp() {
      window.location.href = "${appLink}";
    }
    window.onload = openApp;
  </script>
</head>
<body>
  <div class="card">
    <div class="bar"></div>
    <div class="con">
      <div class="brand">✨ AURA APPS</div>
      <img class="post-img" src="${img}">
      <h2>${title}</h2>
      <p class="desc">${desc}</p>
      <div class="loader"></div>
      <a class="btn btn-p" onclick="openApp()">Open Aura App 🚀</a>
      <a class="btn btn-s" href="${apkLink}">Download APK 📥</a>
      <div class="foot">Aura &copy; 2026</div>
    </div>
  </div>
</body>
</html>`;

  return {
    statusCode: 200,
    headers: { "Content-Type": "text/html;charset=UTF-8" },
    body: htmlOutput
  };
};
