package com.board.backend.auth;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestOAuthController {

    @GetMapping(value = "/test/oauth/success", produces = MediaType.TEXT_HTML_VALUE)
    public String testSuccess(
            @RequestParam String accessToken,
            @RequestParam String refreshToken
    ) {
        return """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>카카오 로그인 테스트</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            max-width: 800px;
                            margin: 50px auto;
                            padding: 20px;
                            background-color: #f5f5f5;
                        }
                        .container {
                            background-color: white;
                            padding: 30px;
                            border-radius: 10px;
                            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                        }
                        h1 {
                            color: #333;
                            border-bottom: 2px solid #4CAF50;
                            padding-bottom: 10px;
                        }
                        .token-section {
                            background-color: #f9f9f9;
                            padding: 15px;
                            border-radius: 5px;
                            margin: 15px 0;
                            word-break: break-all;
                        }
                        .token-label {
                            font-weight: bold;
                            color: #555;
                            margin-bottom: 5px;
                        }
                        .token-value {
                            font-family: monospace;
                            font-size: 12px;
                            background-color: #e8e8e8;
                            padding: 10px;
                            border-radius: 3px;
                            overflow-x: auto;
                        }
                        button {
                            background-color: #4CAF50;
                            color: white;
                            padding: 12px 24px;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                            font-size: 16px;
                            margin: 5px;
                            transition: background-color 0.3s;
                        }
                        button:hover {
                            background-color: #45a049;
                        }
                        button.danger {
                            background-color: #f44336;
                        }
                        button.danger:hover {
                            background-color: #da190b;
                        }
                        button.info {
                            background-color: #2196F3;
                        }
                        button.info:hover {
                            background-color: #0b7dda;
                        }
                        .result {
                            margin-top: 20px;
                            padding: 15px;
                            border-radius: 5px;
                            display: none;
                        }
                        .result.success {
                            background-color: #d4edda;
                            color: #155724;
                            border: 1px solid #c3e6cb;
                        }
                        .result.error {
                            background-color: #f8d7da;
                            color: #721c24;
                            border: 1px solid #f5c6cb;
                        }
                        .button-group {
                            margin-top: 20px;
                            display: flex;
                            flex-wrap: wrap;
                            gap: 10px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🎉 카카오 로그인 성공!</h1>

                        <div class="token-section">
                            <div class="token-label">Access Token:</div>
                            <div class="token-value" id="accessToken">%s</div>
                        </div>

                        <div class="token-section">
                            <div class="token-label">Refresh Token:</div>
                            <div class="token-value" id="refreshToken">%s</div>
                        </div>

                        <div class="button-group">
                            <button class="info" onclick="testAuth()">🔐 인증 테스트 (/test/hello)</button>
                            <button class="info" onclick="getUserInfo()">👤 내 정보 조회 (/api/user/me)</button>
                            <button onclick="refreshAccessToken()">🔄 Access Token 갱신</button>
                            <button class="danger" onclick="logout()">🚪 로그아웃</button>
                        </div>

                        <div id="result" class="result"></div>
                    </div>

                    <script>
                        let currentAccessToken = '%s';
                        let currentRefreshToken = '%s';

                        function showResult(message, isSuccess) {
                            const resultDiv = document.getElementById('result');
                            resultDiv.textContent = message;
                            resultDiv.className = 'result ' + (isSuccess ? 'success' : 'error');
                            resultDiv.style.display = 'block';
                        }

                        async function testAuth() {
                            try {
                                const response = await fetch('http://localhost:8080/test/hello', {
                                    headers: {
                                        'Authorization': 'Bearer ' + currentAccessToken
                                    }
                                });
                                const text = await response.text();
                                showResult('✅ 인증 테스트 성공: ' + text, true);
                            } catch (error) {
                                showResult('❌ 인증 테스트 실패: ' + error.message, false);
                            }
                        }

                        async function getUserInfo() {
                            try {
                                const response = await fetch('http://localhost:8080/api/user/me', {
                                    headers: {
                                        'Authorization': 'Bearer ' + currentAccessToken
                                    }
                                });
                                if (!response.ok) {
                                    throw new Error('HTTP ' + response.status);
                                }
                                const data = await response.json();
                                showResult('✅ 사용자 정보: ' + JSON.stringify(data, null, 2), true);
                            } catch (error) {
                                showResult('❌ 사용자 정보 조회 실패: ' + error.message, false);
                            }
                        }

                        async function refreshAccessToken() {
                            try {
                                const response = await fetch('http://localhost:8080/auth/refresh', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/json'
                                    },
                                    body: JSON.stringify({
                                        refreshToken: currentRefreshToken
                                    })
                                });

                                if (!response.ok) {
                                    throw new Error('HTTP ' + response.status);
                                }

                                const data = await response.json();
                                currentAccessToken = data.accessToken;
                                document.getElementById('accessToken').textContent = currentAccessToken;
                                showResult('✅ Access Token 갱신 성공!', true);
                            } catch (error) {
                                showResult('❌ Access Token 갱신 실패: ' + error.message, false);
                            }
                        }

                        async function logout() {
                            if (!confirm('정말 로그아웃 하시겠습니까?')) {
                                return;
                            }

                            try {
                                const response = await fetch('http://localhost:8080/auth/logout', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/json'
                                    },
                                    body: JSON.stringify({
                                        refreshToken: currentRefreshToken
                                    })
                                });

                                if (!response.ok) {
                                    throw new Error('HTTP ' + response.status);
                                }

                                const text = await response.text();
                                showResult('✅ ' + text + ' - 3초 후 로그인 페이지로 이동합니다.', true);

                                setTimeout(() => {
                                    window.location.href = 'http://localhost:8080/oauth2/authorization/kakao';
                                }, 3000);
                            } catch (error) {
                                showResult('❌ 로그아웃 실패: ' + error.message, false);
                            }
                        }
                    </script>
                </body>
                </html>
                """
                .formatted(accessToken, refreshToken, accessToken, refreshToken);
    }
}
