const API_BASE = "http://localhost:8090";
const TOKEN_KEY = "elastic_assistant_token";
const $ = id => document.getElementById(id);

function token(){ return localStorage.getItem(TOKEN_KEY); }
function saveToken(t){ localStorage.setItem(TOKEN_KEY,t); }
function clearToken(){ localStorage.removeItem(TOKEN_KEY); }

function message(text,error=false){
  const box=$("message");
  box.textContent=text;
  box.classList.remove("hidden");
  box.style.background=error ? "#fff1f1" : "#eef5ff";
  box.style.borderColor=error ? "#f0bcbc" : "#cfe0ff";
}
function hideMessage(){ $("message").classList.add("hidden"); }

async function api(path,options={}){
  const headers=new Headers(options.headers || {});
  if(token()) headers.set("Authorization","Bearer "+token());
  return fetch(API_BASE+path,{...options,headers});
}

function showDashboard(){
  $("authSection").classList.add("hidden");
  $("dashboardSection").classList.remove("hidden");
  $("logoutBtn").classList.remove("hidden");
  health();
  loadKnowledge();
}
function showAuth(){
  $("authSection").classList.remove("hidden");
  $("dashboardSection").classList.add("hidden");
  $("logoutBtn").classList.add("hidden");
}

async function health(){
  try{
    const r=await fetch(API_BASE+"/health");
    const d=await r.json();
    $("backendStatus").textContent=(r.ok && d.status==="UP") ? "UP" : "Unavailable";
  }catch(e){ $("backendStatus").textContent="Unavailable"; }
}

$("registerForm").addEventListener("submit",async e=>{
  e.preventDefault(); hideMessage();
  const body={
    fullName:$("registerName").value.trim(),
    email:$("registerEmail").value.trim(),
    password:$("registerPassword").value
  };
  try{
    const r=await fetch(API_BASE+"/auth/register",{
      method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(body)
    });
    const d=await r.json();
    if(!r.ok) throw new Error(d.message || "Registration failed");
    message(d.message || "Registration successful");
    $("registerForm").reset();
  }catch(err){ message(err.message,true); }
});

$("loginForm").addEventListener("submit",async e=>{
  e.preventDefault(); hideMessage();
  const body={
    email:$("loginEmail").value.trim(),
    password:$("loginPassword").value
  };
  try{
    const r=await fetch(API_BASE+"/auth/login",{
      method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(body)
    });
    const d=await r.json();
    if(!r.ok || !d.token) throw new Error(d.message || "Login failed");
    saveToken(d.token);
    $("loginForm").reset();
    showDashboard();
    message("Login successful");
  }catch(err){ message(err.message,true); }
});

$("logoutBtn").addEventListener("click",()=>{
  clearToken();
  $("answerBox").classList.add("hidden");
  $("sourcesBox").classList.add("hidden");
  showAuth();
  message("Logged out");
});

$("uploadForm").addEventListener("submit",async e=>{
  e.preventDefault(); hideMessage();
  const file=$("fileInput").files[0];
  if(!file){ message("Please select a file.",true); return; }
  const form=new FormData();
  form.append("file",file);
  try{
    const r=await api("/documents/upload",{method:"POST",body:form});
    const text=await r.text();
    if(!r.ok) throw new Error(text || "Upload failed");
    $("uploadResult").textContent=text;
    $("uploadResult").classList.remove("hidden");
    $("fileInput").value="";
    message("Document uploaded successfully");
    loadKnowledge();
  }catch(err){ message(err.message,true); }
});

$("askForm").addEventListener("submit",async e=>{
  e.preventDefault(); hideMessage();
  const question=$("questionInput").value.trim();
  if(!question){ message("Please enter a question.",true); return; }
  try{
    const r=await api("/rag/ask",{
      method:"POST",
      headers:{"Content-Type":"application/json"},
      body:JSON.stringify({question})
    });
    const d=await r.json();
    if(!r.ok) throw new Error(d.message || "RAG request failed");
    $("answerText").textContent=d.answer || "No answer returned.";
    $("answerBox").classList.remove("hidden");
    renderSources(d.sources || []);
    message("Answer generated successfully");
  }catch(err){ message(err.message,true); }
});

function renderSources(sources){
  const list=$("sourcesList");
  list.innerHTML="";
  if(!sources.length){
    list.innerHTML="<p class='muted'>No sources returned.</p>";
    $("sourcesBox").classList.remove("hidden");
    return;
  }
  sources.forEach(s=>{
    const div=document.createElement("div");
    div.className="source-card";
    div.innerHTML=
      "<div class='source-title'>"+esc(s.title || "Untitled")+"</div>"+
      "<div class='source-meta'>Source: "+esc(s.source || "N/A")+
      " | Chunk: "+esc(s.chunkNumber ?? "N/A")+"</div>"+
      "<div class='source-content'>"+esc(s.content || "")+"</div>";
    list.appendChild(div);
  });
  $("sourcesBox").classList.remove("hidden");
}

$("refreshKnowledgeBtn").addEventListener("click",loadKnowledge);

async function loadKnowledge(){
  if(!token()) return;
  try{
    const r=await api("/knowledge");
    if(r.status===401 || r.status===403){
      clearToken(); showAuth(); message("Session expired. Login again.",true); return;
    }
    const d=await r.json();
    if(!r.ok) throw new Error(d.message || "Could not load knowledge base");
    const list=$("knowledgeList");
    list.innerHTML="";
    if(!d.length){ list.innerHTML="<p class='muted'>No documents indexed.</p>"; return; }
    d.forEach(doc=>{
      const div=document.createElement("div");
      div.className="knowledge-item";
      div.innerHTML=
        "<div class='knowledge-title'>"+esc(doc.title || "Untitled")+"</div>"+
        "<div class='knowledge-meta'>Source: "+esc(doc.source || "N/A")+
        " | Chunk: "+esc(doc.chunkNumber ?? "N/A")+"</div>";
      list.appendChild(div);
    });
  }catch(err){ message(err.message,true); }
}

function esc(v){
  return String(v)
    .replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;")
    .replaceAll('"',"&quot;").replaceAll("'","&#039;");
}

if(token()) showDashboard(); else showAuth();
