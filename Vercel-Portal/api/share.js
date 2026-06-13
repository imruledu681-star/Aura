// 🚀 AURA DYNAMIC SHARE PORTAL - 100% FREE, SEAMLESS, & DATABASE-FREE
// This node.js script runs inside Vercel's serverless environment, reading parameters
// and rendering dynamic, high-fidelity SEO metadata preview cards instantly!

const url = require('url');

module.exports = async function (req, res) {
    const parsedUrl = url.parse(req.url, true);
    const query = parsedUrl.query;

    // 1. Extract and sanitize custom query parameters
    const postId = query.postId || '';
    const rawName = query.n || query.t || '';
    const rawDesc = query.d || '';
    const rawImage = query.i || '';

    // 2. Safely decode UTF-8 Base64 strings (compatible with Android UrlSafe encoding)
    function safeBase64Decode(str) {
        if (!str) return '';
        try {
            // Convert UrlSafe chars
            let clean = str.replace(/-/g, '+').replace(/_/g, '/');
            // Auto pad to multiple of 4
            while (clean.length % 4 !== 0) {
                clean += '=';
            }
            return Buffer.from(clean, 'base64').toString('utf8');
        } catch (e) {
            try {
                return Buffer.from(str, 'base64').toString('utf8');
            } catch (e2) {
                return str; // Fallback
            }
        }
    }

    const authorName = safeBase64Decode(rawName) || 'অরা সদস্য (Aura Member)';
    const postText = safeBase64Decode(rawDesc) || 'অরা অ্যাপ্লিকেশনে চমৎকার একটি মুহূর্ত শেয়ার করলেন।';
    const imageSrc = safeBase64Decode(rawImage);

    // Filter local resources like "content://" or "file://" to custom elegant fallback
    const isLocalResource = !imageSrc.startsWith('http');
    const displayImage = (imageSrc && !isLocalResource) 
        ? imageSrc 
        : 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=800&q=80';

    // 3. Prepare display title and rich description
    const fullPageTitle = `${authorName} - Aura`;
    const previewDescription = postText.length > 140 ? postText.substring(0, 137) + '...' : postText;

    // Send the dynamic HTML
    res.setHeader('Content-Type', 'text/html; charset=utf-8');
    res.status(200).send(`<!DOCTYPE html>
<html lang="bn">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${fullPageTitle}</title>
    
    <!-- Open Graph OG tags for Rich Link Previews (Facebook, WhatsApp, Telegram, Discord) -->
    <meta property="og:title" content="💜 ${authorName}'s Post on Aura" />
    <meta property="og:description" content="${previewDescription}" />
    <meta property="og:image" content="${displayImage}" />
    <meta property="og:image:width" content="1200" />
    <meta property="og:image:height" content="630" />
    <meta property="og:type" content="article" />
    <meta property="og:site_name" content="Aura Social" />
    
    <!-- Twitter Post Meta Prep -->
    <meta name="twitter:card" content="summary_large_image" />
    <meta name="twitter:title" content="💜 ${authorName}'s Post on Aura" />
    <meta name="twitter:description" content="${previewDescription}" />
    <meta name="twitter:image" content="${displayImage}" />

    <!-- Premium Google Font Styling -->
    <link href="https://fonts.googleapis.com/css2?family=Hind+Siliguri:wght@400;600;700&family=Plus+Jakarta+Sans:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <style>
        :root {
            --primary: #7C4DFF;
            --primary-light: #F3E8FF;
            --primary-dark: #651FFF;
            --secondary: #00E676;
            --bg-page: #FFFFFF;
            --bg-accent: #FAF9FF;
            --text-main: #1A103C;
            --text-sub: #5F5670;
            --card-border: #E9E3FA;
            --shadow: 0 12px 36px rgba(124, 77, 255, 0.08);
            --shadow-hover: 0 18px 48px rgba(124, 77, 255, 0.16);
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Plus Jakarta Sans', 'Hind Siliguri', sans-serif;
            background-color: var(--bg-page);
            color: var(--text-main);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            overflow-x: hidden;
            -webkit-font-smoothing: antialiased;
        }

        .blur-dot {
            position: absolute;
            width: 350px;
            height: 350px;
            background: rgba(124, 77, 255, 0.12);
            filter: blur(120px);
            border-radius: 50%;
            z-index: -1;
            pointer-events: none;
        }
        .blur-dot-1 { top: -50px; left: -100px; }
        .blur-dot-2 { bottom: 10%; right: -100px; }

        .portal-wrapper {
            width: 100%;
            max-width: 685px;
            padding: 40px 20px;
            display: flex;
            flex-direction: column;
            gap: 32px;
        }

        header {
            text-align: center;
            animation: fadeInDown 0.8s ease;
        }

        .logo-container {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 12px;
            background: linear-gradient(135deg, #FAF8FF 0%, #F5F1FF 100%);
            border: 2px solid var(--primary-light);
            padding: 12px 28px;
            border-radius: 30px;
            box-shadow: 0 8px 24px rgba(124, 77, 255, 0.06);
            margin-bottom: 20px;
        }

        .logo-orb {
            position: relative;
            width: 36px;
            height: 36px;
            background: var(--primary);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 12px rgba(124, 77, 255, 0.3);
            color: #FFF;
            font-size: 18px;
            animation: pulse-ring 3s infinite;
        }

        .logo-text {
            font-size: 28px;
            font-weight: 800;
            color: var(--primary);
            letter-spacing: -1px;
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .hero-title {
            font-size: 24px;
            font-weight: 800;
            line-height: 1.3;
            margin-bottom: 8px;
            color: var(--text-main);
        }

        .hero-subtitle {
            font-size: 15px;
            color: var(--text-sub);
            max-width: 500px;
            margin: 0 auto;
            line-height: 1.5;
        }

        /* Responsive Dynamic Preview Section */
        .post-preview-section {
            background: #FFFFFF;
            border: 2px solid var(--primary-light);
            border-radius: 28px;
            padding: 24px;
            box-shadow: var(--shadow);
            position: relative;
            overflow: hidden;
            animation: fadeInUp 0.8s ease;
        }

        .post-preview-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-bottom: 1px solid var(--primary-light);
            padding-bottom: 16px;
            margin-bottom: 16px;
        }

        .post-creator-info {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .avatar-frame {
            width: 48px;
            height: 48px;
            background: var(--primary);
            color: #FFFFFF;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            font-weight: 800;
            border: 2px solid var(--primary-light);
            box-shadow: 0 4px 10px rgba(124, 77, 255, 0.15);
        }

        .creator-names {
            display: flex;
            flex-direction: column;
        }

        .creator-title {
            font-size: 16px;
            font-weight: 700;
            color: var(--text-main);
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .creator-title i {
            color: var(--primary);
            font-size: 13px;
        }

        .badge-pill {
            background: var(--primary-light);
            color: var(--primary);
            font-size: 11px;
            font-weight: 700;
            padding: 2px 10px;
            border-radius: 12px;
            text-transform: uppercase;
        }

        .creator-handle {
            font-size: 12px;
            color: var(--text-sub);
        }

        .post-content {
            font-size: 15px;
            line-height: 1.6;
            color: var(--text-main);
            margin-bottom: 18px;
            font-weight: 500;
            white-space: pre-wrap;
            word-wrap: break-word;
        }

        .post-image {
            width: 100%;
            max-height: 380px;
            object-fit: cover;
            border-radius: 20px;
            border: 2px solid var(--primary-light);
            box-shadow: 0 4px 16px rgba(124, 77, 255, 0.05);
            margin-bottom: 20px;
        }

        .launch-action-bar {
            background: var(--bg-accent);
            border-radius: 20px;
            padding: 24px;
            text-align: center;
            border: 1.5px dashed var(--primary-light);
        }

        .launch-title {
            font-size: 16px;
            font-weight: 800;
            margin-bottom: 14px;
            color: var(--primary);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
        }

        .section-headline {
            font-size: 18px;
            font-weight: 800;
            letter-spacing: -0.5px;
            display: flex;
            align-items: center;
            gap: 10px;
            color: var(--text-main);
            margin-bottom: 18px;
        }

        .section-headline i {
            color: var(--primary);
        }

        .download-options-list {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }

        .download-card {
            background: #FFFFFF;
            border: 2px solid var(--card-border);
            border-radius: 24px;
            padding: 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            cursor: pointer;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            overflow: hidden;
            box-shadow: var(--shadow);
            text-decoration: none;
            color: inherit;
        }

        .download-card:hover {
            border-color: var(--primary);
            transform: translateY(-4px);
            box-shadow: var(--shadow-hover);
        }

        .download-card.primary-choice {
            border-color: var(--primary);
            background: linear-gradient(to right, #FFFFFF, var(--bg-accent));
        }

        .download-card.primary-choice::before {
            content: 'প্রস্তাবিত / RECOMMENDED';
            position: absolute;
            top: 0;
            right: 24px;
            font-size: 8px;
            font-weight: 800;
            color: #FFF;
            background: var(--primary);
            padding: 4px 12px;
            border-radius: 0 0 10px 10px;
            letter-spacing: 0.5px;
        }

        .download-card-left {
            display: flex;
            align-items: center;
            gap: 18px;
        }

        .download-icon-box {
            width: 52px;
            height: 52px;
            border-radius: 18px;
            background: var(--primary-light);
            color: var(--primary);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            transition: transform 0.3s ease;
        }

        .download-card:hover .download-icon-box {
            transform: scale(1.1) rotate(5deg);
        }

        .download-details {
            display: flex;
            flex-direction: column;
            gap: 2px;
            text-align: left;
        }

        .download-title {
            font-size: 16px;
            font-weight: 800;
            color: var(--text-main);
        }

        .download-subtitle {
            font-size: 12px;
            color: var(--text-sub);
            line-height: 1.4;
        }

        .arrow-indicator {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: var(--primary-light);
            color: var(--primary);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            transition: all 0.3s ease;
        }

        .download-card:hover .arrow-indicator {
            background: var(--primary);
            color: #FFFFFF;
            transform: translateX(4px);
        }

        /* Premium Buttons */
        .btn-action {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            background: var(--primary);
            color: #FFFFFF !important;
            border: none;
            padding: 16px 32px;
            border-radius: 50px;
            font-size: 15px;
            font-weight: 800;
            cursor: pointer;
            box-shadow: 0 8px 24px rgba(124, 77, 255, 0.25);
            transition: all 0.3s ease;
            text-decoration: none;
        }

        .btn-action:hover {
            background: var(--primary-dark);
            transform: scale(1.03);
            box-shadow: 0 12px 30px rgba(124, 77, 255, 0.35);
        }

        .btn-action-outline {
            background: transparent;
            color: var(--primary) !important;
            border: 2px solid var(--primary);
            box-shadow: none;
        }

        .btn-action-outline:hover {
            background: var(--primary-light);
            box-shadow: none;
        }

        .install-guide-card {
            background: var(--bg-accent);
            border: 1px solid var(--primary-light);
            border-radius: 28px;
            padding: 24px;
            box-shadow: var(--shadow);
        }

        .guide-tabs {
            display: flex;
            background: var(--primary-light);
            padding: 6px;
            border-radius: 16px;
            margin-bottom: 20px;
        }

        .guide-tab-btn {
            flex: 1;
            padding: 10px;
            border: none;
            background: transparent;
            color: var(--text-sub);
            font-weight: 700;
            font-size: 13px;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.2s ease;
        }

        .guide-tab-btn.active {
            background: #FFFFFF;
            color: var(--primary);
            box-shadow: 0 4px 12px rgba(124, 77, 255, 0.08);
        }

        .guide-pane {
            display: none;
            text-align: left;
            animation: fadeIn 0.4s ease;
        }

        .guide-pane.active {
            display: block;
        }

        .step-list {
            display: flex;
            flex-direction: column;
            gap: 16px;
        }

        .step-item {
            display: flex;
            gap: 16px;
            align-items: flex-start;
        }

        .step-num {
            width: 28px;
            height: 28px;
            background: var(--primary);
            color: #FFFFFF;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 800;
            font-size: 12px;
            flex-shrink: 0;
            margin-top: 2px;
        }

        .step-text {
            font-size: 14px;
            line-height: 1.5;
            color: var(--text-main);
        }

        .step-text strong {
            color: var(--primary-dark);
        }

        .redirect-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(26, 16, 60, 0.95);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            z-index: 1000;
            color: #FFFFFF;
            transition: opacity 0.5s ease;
        }

        .redirect-box {
            text-align: center;
            max-width: 340px;
            padding: 40px 24px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 32px;
            border: 1px solid rgba(255, 255, 255, 0.1);
            backdrop-filter: blur(10px);
            animation: scaleUp 0.5s ease;
        }

        .spinner-ring {
            width: 48px;
            height: 48px;
            border: 4px solid rgba(243, 232, 255, 0.15);
            border-top-color: var(--primary-light);
            border-radius: 50%;
            animation: spin 1s linear infinite;
            display: inline-block;
            margin-bottom: 24px;
        }

        .redirect-title {
            font-size: 20px;
            font-weight: 800;
            margin-bottom: 8px;
            background: linear-gradient(135deg, #FFF 0%, var(--primary-light) 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .redirect-desc {
            font-size: 13px;
            color: rgba(255, 255, 255, 0.7);
            line-height: 1.5;
            margin-bottom: 24px;
        }

        footer {
            text-align: center;
            padding: 45px 20px;
            color: var(--text-sub);
            font-size: 12px;
            display: flex;
            flex-direction: column;
            gap: 12px;
            width: 100%;
            margin-top: auto;
            border-top: 1px solid var(--primary-light);
        }

        .lang-disclaimer {
            font-size: 12px;
            background: var(--bg-accent);
            padding: 12px 18px;
            border-radius: 12px;
            border: 1px solid var(--primary-light);
            line-height: 1.4;
        }

        @keyframes fadeInDown {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes fadeInUp {
            from { opacity: 0; transform: translateY(25px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
        @keyframes scaleUp {
            from { opacity: 0; transform: scale(0.9); }
            to { opacity: 1; transform: scale(1); }
        }
        @keyframes spin {
            100% { transform: rotate(360deg); }
        }
        @keyframes pulse-ring {
            0% { box-shadow: 0 0 0 0 rgba(124, 77, 255, 0.4); }
            70% { box-shadow: 0 0 0 15px rgba(124, 77, 255, 0); }
            100% { box-shadow: 0 0 0 0 rgba(124, 77, 255, 0); }
        }

        @media (max-width: 480px) {
            .portal-wrapper { padding: 20px 14px; gap: 24px; }
            .hero-title { font-size: 20px; }
            .hero-subtitle { font-size: 13px; }
            .download-card { padding: 16px; }
            .download-icon-box { width: 44px; height: 44px; font-size: 18px; }
            .download-title { font-size: 14px; }
            .download-subtitle { font-size: 11px; }
            .post-preview-section { padding: 16px; border-radius: 20px; }
            .avatar-frame { width: 42px; height: 42px; font-size: 18px; }
            .creator-title { font-size: 14px; }
            .post-content { font-size: 14px; }
        }
    </style>
</head>
<body>

    <div class="blur-dot blur-dot-1"></div>
    <div class="blur-dot blur-dot-2"></div>

    <!-- Active Android Deep Link Redirect Overlay -->
    <div id="redirectOverlay" class="redirect-overlay">
        <div class="redirect-box">
            <div class="spinner-ring"></div>
            <h3 class="redirect-title">Aura সেশন চালু হচ্ছে...</h3>
            <p class="redirect-desc">Aura অ্যাপে পোস্টটি সরাসরি ওপেন করা হচ্ছে। অনুগ্রহপূর্বক অপেক্ষা করুন...</p>
            <button class="btn-action btn-action-outline" onclick="cancelRedirect()" style="padding: 10px 24px; font-size: 13px; border-color: rgba(255,255,255,0.3); color: white !important;">
                ম্যানুয়ালি ব্রাউজারে দেখুন / Skip
            </button>
        </div>
    </div>

    <div class="portal-wrapper">
        
        <header>
            <div class="logo-container">
                <div class="logo-orb"><i class="fa-solid fa-wand-magic-sparkles"></i></div>
                <div class="logo-text">Aura</div>
            </div>
            <h1 class="hero-title">অরা শেয়ারড পোস্ট প্রিভিউ</h1>
            <p class="hero-subtitle">বন্ধুর শেয়ার করা পোস্টটি সরাসরি নিচে দেখুন ও আপনার ডিভাইসের Aura অ্যাপলেট দিয়ে যুক্ত হোন।</p>
        </header>

        <!-- Dynamic Post Preview -->
        <div class="post-preview-section">
            <div class="post-preview-header">
                <div class="post-creator-info">
                    <div class="avatar-frame" id="avatarBox">${authorName.substring(0, 1).toUpperCase()}</div>
                    <div class="creator-names">
                        <span class="creator-title">
                            <span>${authorName}</span> 
                            <i class="fa-solid fa-circle-check" title="Verified Member"></i>
                        </span>
                        <span class="creator-handle">@aura_member</span>
                    </div>
                </div>
                <span class="badge-pill">শেয়ারকৃত পোস্ট</span>
            </div>
            
            <div class="post-content">${postText}</div>
            
            ${imageSrc && !isLocalResource ? `<img src="${imageSrc}" alt="Shared Image" class="post-image">` : ''}

            <div class="launch-action-bar">
                <h4 class="launch-title"><i class="fa-solid fa-mobile-screen-button"></i> আপনার ফোনে কি Aura অ্যাপটি আছে?</h4>
                <div style="display: flex; gap: 12px; flex-wrap: wrap; justify-content: center;">
                    <a href="javascript:void(0)" onclick="triggerNativeDeepLink()" class="btn-action">
                        <i class="fa-solid fa-rocket"></i> সরাসরি অ্যাপে খুলুন
                    </a>
                    <button class="btn-action btn-action-outline" onclick="scrollToInstall()" style="background: white;">
                        <i class="fa-solid fa-download"></i> অ্যাপ ডাউনলোড করুন
                    </button>
                </div>
            </div>
        </div>

        <!-- Download Buttons -->
        <div id="downloadSection">
            <h2 class="section-headline">
                <i class="fa-solid fa-download"></i> অরা অ্যাপ ইন্সটল করুন (Aura Applet Downloads)
            </h2>
            
            <div class="download-options-list">
                <!-- 1. Direct APK from Vercel Edge -->
                <a href="/app-debug.apk" class="download-card primary-choice" download="app-debug.apk">
                    <div class="download-card-left">
                        <div class="download-icon-box" style="background: #E8F5E9; color: #4CAF50;">
                            <i class="fa-brands fa-android"></i>
                        </div>
                        <div class="download-details">
                            <span class="download-title">সরাসরি APK ডাউনলোড করুন</span>
                            <span class="download-subtitle">Direct Installer (app-debug.apk). উচ্চগতির ও নিরাপদ মাধ্যম।</span>
                        </div>
                    </div>
                    <div class="arrow-indicator"><i class="fa-solid fa-arrow-down"></i></div>
                </a>

                <!-- 2. Uncompressed Standard ZIP (Edge CDN) -->
                <a href="/app-debug-apk.zip" class="download-card" download="app-debug-apk.zip">
                    <div class="download-card-left">
                        <div class="download-icon-box" style="background: #FFF3E0; color: #FF9800;">
                            <i class="fa-solid fa-file-zipper"></i>
                        </div>
                        <div class="download-details">
                            <span class="download-title">ZIP ফরম্যাটে ডাউনলোড করুন</span>
                            <span class="download-subtitle">Compressed Archive (app-debug-apk.zip). ফোনে এক্সট্রাক্ট করে নিন।</span>
                        </div>
                    </div>
                    <div class="arrow-indicator"><i class="fa-solid fa-arrow-down"></i></div>
                </a>

                <!-- 3. Safe Binary fallback -->
                <a href="/app-debug-apk.bin" class="download-card" download="app-debug-apk.bin">
                    <div class="download-card-left">
                        <div class="download-icon-box" style="background: #E1F5FE; color: #0288D1;">
                            <i class="fa-solid fa-box-open"></i>
                        </div>
                        <div class="download-details">
                            <span class="download-title">বিকল্প বাইনারি (.bin) ডাউনলোড</span>
                            <span class="download-subtitle">Alternative Binary. প্রক্সি ব্লক এড়াতে ফাইলটি রিনেম (.apk) করুন।</span>
                        </div>
                    </div>
                    <div class="arrow-indicator"><i class="fa-solid fa-arrow-down"></i></div>
                </a>
            </div>
        </div>

        <!-- Setup Guide Cards -->
        <div class="install-guide-card" id="installGuide">
            <h2 class="section-headline" style="font-size: 16px; margin-bottom: 20px;">
                <i class="fa-solid fa-circle-info"></i> কিভাবে সফলভাবে ইনস্টল করবেন (Setup Guide)
            </h2>
            
            <div class="guide-tabs">
                <button class="guide-tab-btn active" onclick="switchTab('bn')">বাংলা গাইড</button>
                <button class="guide-tab-btn" onclick="switchTab('en')">English Guide</button>
            </div>

            <!-- Bangla Pane -->
            <div id="pane-bn" class="guide-pane active">
                <div class="step-list">
                    <div class="step-item">
                        <div class="step-num">১</div>
                        <div class="step-text">উপরে থাকা সবুজ কার্ডের <strong>সরাসরি APK ডাউনলোড</strong> বাটনে ক্লিক করে ফাইলটি ডাউনলোড ফোল্ডারে কার্ড সেভ করুন।</div>
                    </div>
                    <div class="step-item">
                        <div class="step-num">২</div>
                        <div class="step-text">ডাউনলোড সম্পূর্ণ হলে বিজ্ঞপ্তিতে অথবা ফাইল ম্যানেজারে গিয়ে ফাইলে ক্লিক করুন। যদি ফোন ব্রাউজার ব্লক দেয়, তবে সেটিংস থেকে <strong>"Allow from this source"</strong> চালু করুন।</div>
                    </div>
                    <div class="step-item">
                        <div class="step-num">৩</div>
                        <div class="step-text">যদি আপনার ফোন সরাসরি APK ফাইল ডাউনলোড ব্লক করে, তাহলে ২য় বিকল্প <strong>ZIP ফাইলটি</strong> ডাউনলোড করে নিয়ে এক্সট্রাক্ট করুন ও ফাইলটি ইন্সটল করুন।</div>
                    </div>
                    <div class="step-item">
                        <div class="step-num">৪</div>
                        <div class="step-text">আর যদি তাও সমস্যা করে, তাহলে ৩য় বিকল্প <strong>BIN ফাইলটি</strong> ডাউনলোড ফাইলসে নিয়ে ফাইলের নাম রি-নেম করে শেষ অংশ <code>.bin</code> থেকে <code>.apk</code> করুন ও ইনস্টল শেষ করুন।</div>
                    </div>
                </div>
            </div>

            <!-- English Pane -->
            <div id="pane-en" class="guide-pane">
                <div class="step-list">
                    <div class="step-item">
                        <div class="step-num">1</div>
                        <div class="step-text">Click the <strong>Direct APK Download</strong> at the top green option card and grab the setup file instantly.</div>
                    </div>
                    <div class="step-item">
                        <div class="step-num">2</div>
                        <div class="step-text">Open your notifications or File Manager, click the downloaded file. Allow installer privileges if system requests <strong>"Install Unknown Apps"</strong> permission.</div>
                    </div>
                    <div class="step-item">
                        <div class="step-num">3</div>
                        <div class="step-text">In case your web browser restricts downloading direct APK files, download the <strong>Compressed ZIP format</strong>, extract it to find the inner APK, and tap to trigger installation.</div>
                    </div>
                    <div class="step-item">
                        <div class="step-num">4</div>
                        <div class="step-text">If any firewall blocks remain, download the <strong>Alternative Binary (.bin)</strong>, rename the extension from <code>.bin</code> to <code>.apk</code> locally and install easily!</div>
                    </div>
                </div>
            </div>
        </div>

        <footer>
            <div class="lang-disclaimer">
                💡 <strong>Dynamic Vercel Engine is Active:</strong> This sharing portal operates fully client-side and serverless, rendering beautiful customized social previews without requiring any databases or Appwrite accounts.
            </div>
            <div>Aura Client Redirection Hub v1.5 &bull; 2026 Social Suite</div>
        </footer>

    </div>

    <script>
        const postId = "${postId}";

        // Android Custom Intent Trigger Strategy
        function triggerNativeDeepLink() {
            if (postId) {
                // Try Custom App Scheme directly
                window.location.href = \`aura://post/\${postId}?postId=\${postId}\`;
                
                // Secondary backup scheme
                setTimeout(() => {
                    const currentQuery = window.location.search;
                    window.location.href = \`aura://post/\${postId}\${currentQuery}\`;
                }, 600);
            }
        }

        // Close Redirect screen
        function cancelRedirect() {
            const overlay = document.getElementById('redirectOverlay');
            if (overlay) {
                overlay.style.opacity = '0';
                setTimeout(() => {
                    overlay.style.display = 'none';
                }, 500);
            }
        }

        function scrollToInstall() {
            document.getElementById('downloadSection').scrollIntoView({ behavior: 'smooth' });
        }

        function switchTab(lang) {
            document.querySelectorAll('.guide-tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.guide-pane').forEach(p => p.classList.remove('active'));

            if (lang === 'bn') {
                document.querySelector('.guide-tab-btn[onclick="switchTab(\\'bn\\')"]').classList.add('active');
                document.getElementById('pane-bn').classList.add('active');
            } else {
                document.querySelector('.guide-tab-btn[onclick="switchTab(\\'en\\')"]').classList.add('active');
                document.getElementById('pane-en').classList.add('active');
            }
        }

        // Run auto redirect on DOM Load
        window.addEventListener('DOMContentLoaded', () => {
            if (postId) {
                triggerNativeDeepLink();
                // Close overlay cleanly after 4 seconds if no action is registered
                setTimeout(cancelRedirect, 4000);
            } else {
                cancelRedirect();
            }
        });
    </script>
</body>
</html>`);
};
