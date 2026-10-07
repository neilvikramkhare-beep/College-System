import sys

nav_items = [
    ('dashboard', 'fas fa-chart-pie', 'Dashboard'),
    ('colleges', 'fas fa-building', 'Colleges'),
    ('students', 'fas fa-user-graduate', 'Students'),
    ('courses', 'fas fa-book-open', 'Courses'),
    ('faculty', 'fas fa-chalkboard-teacher', 'Faculty'),
    ('admissions', 'fas fa-file-signature', 'Admissions'),
    ('attendances', 'fas fa-calendar-check', 'Attendances'),
    ('examinations', 'fas fa-file-alt', 'Examinations'),
    ('reviews', 'fas fa-star', 'Reviews'),
    ('syllabuses', 'fas fa-book', 'Syllabuses'),
    ('timetables', 'fas fa-clock', 'Timetables')
]

html_template = '''<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>University Administration Portal</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        tailwind.config = { theme: { extend: { colors: { brand: { dark: '#0f172a', primary: '#1e3a8a', secondary: '#3b82f6', light: '#e0f2fe' } } } } }
    </script>
    <style>
        .section-view { display: none; }
        .section-view.active { display: block; }
        .modal { display: none; }
        .modal.active { display: flex; }
    </style>
</head>
<body class="bg-gray-100 flex h-screen overflow-hidden font-sans">
    <aside class="w-64 bg-brand-dark text-white flex flex-col shadow-xl z-20 overflow-y-auto">
        <div class="flex items-center justify-center h-20 border-b border-gray-700 shrink-0">
            <i class="fas fa-university text-3xl text-brand-secondary mr-3"></i>
            <h1 class="text-xl font-bold uppercase tracking-wider">EduManage</h1>
        </div>
        <nav class="flex-1 px-4 py-6 space-y-2">
'''

for id, icon, name in nav_items:
    html_template += f'''            <button onclick="navigate('{id}')" id="nav-{id}" class="nav-btn w-full flex items-center px-4 py-3 hover:bg-gray-800 rounded-lg text-left transition-colors">
                <i class="{icon} w-6"></i> {name}
            </button>\n'''

html_template += '''        </nav>
    </aside>
    <main class="flex-1 flex flex-col h-screen overflow-hidden">
        <header class="h-20 bg-white shadow-sm flex items-center justify-between px-8 z-10 shrink-0">
            <h2 id="page-title" class="text-2xl font-semibold text-gray-800">Overview Dashboard</h2>
            <div class="flex items-center space-x-4">
                <div id="connection-status" class="text-sm font-bold text-gray-500">Checking connection...</div>
                <button onclick="refreshData()" class="p-2 text-gray-400 hover:text-brand-secondary transition-colors" title="Refresh Data">
                    <i class="fas fa-sync-alt"></i>
                </button>
            </div>
        </header>
        <div class="flex-1 overflow-x-hidden overflow-y-auto bg-gray-50 p-8">
            <div id="error-banner" class="hidden bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-6" role="alert">
                <p class="font-bold">Backend Connection Failed</p>
                <p>Could not connect to the Java backend.</p>
            </div>
            
            <div id="dashboard" class="section-view active space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div class="bg-white rounded-xl shadow-sm p-6 border border-gray-100 flex items-center">
                        <div>
                            <p class="text-sm text-gray-500 font-medium">Database Status</p>
                            <p id="db-status" class="text-2xl font-bold text-green-500">Online</p>
                        </div>
                    </div>
                </div>
                
                <div class="mt-8 bg-white rounded-xl shadow-sm p-6 border border-gray-100">
                    <h3 class="text-lg font-bold text-gray-800 mb-4">College Operations Dashboard</h3>
                    <div class="flex flex-wrap gap-4">
                        <button onclick="runOp('addDepartment')" class="bg-indigo-500 text-white px-4 py-2 rounded shadow">Add Department (test)</button>
                        <button onclick="runOp('addFaculty')" class="bg-indigo-500 text-white px-4 py-2 rounded shadow">Add Faculty (test)</button>
                        <button onclick="runOp('addStudent')" class="bg-indigo-500 text-white px-4 py-2 rounded shadow">Add Student (test)</button>
                        <button onclick="runOp('addCourse')" class="bg-indigo-500 text-white px-4 py-2 rounded shadow">Add Course (test)</button>
                    </div>
                    <p class="text-sm text-gray-500 mt-4 italic">Check the Java console to see the output from these operations!</p>
                </div>
            </div>
'''

def generate_section(id, title, headers, fields):
    html = f'''
            <!-- {title.upper()} SECTION -->
            <div id="{id}" class="section-view">
                <div class="flex justify-between items-center mb-6">
                    <h3 class="text-xl font-bold text-gray-800">{title} Directory</h3>
                    <button onclick="openModal('{id}-modal')" class="bg-brand-secondary hover:bg-blue-600 text-white px-4 py-2 rounded-lg shadow-sm transition-colors font-medium flex items-center">
                        <i class="fas fa-plus mr-2"></i> Add {title}
                    </button>
                </div>
                <div class="bg-white shadow-sm rounded-xl border border-gray-100 overflow-hidden">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
'''
    for h, _ in headers:
        html += f'''                                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">{h}</th>\n'''
    html += f'''                            </tr>
                        </thead>
                        <tbody id="{id}-table-body" class="bg-white divide-y divide-gray-200"></tbody>
                    </table>
                </div>
            </div>
'''
    return html

sections_config = {
    'colleges': ('College', [('Name', 'name'), ('Location', 'location'), ('Established', 'establishedYear')], [('name', 'text'), ('location', 'text'), ('establishedYear', 'number')]),
    'students': ('Student', [('Name', 'name'), ('Age', 'age'), ('Enrolled Courses', 'enrolledCourses')], [('name', 'text'), ('age', 'number'), ('enrolledCourses', 'text')]),
    'courses': ('Course', [('Course Name', 'courseName'), ('Course Code', 'courseCode'), ('Credits', 'credits')], [('courseName', 'text'), ('courseCode', 'text'), ('credits', 'number')]),
    'faculty': ('Faculty', [('Name', 'name'), ('Age', 'age'), ('Department', 'department'), ('Designation', 'designation')], [('name', 'text'), ('age', 'number'), ('department', 'text'), ('designation', 'text')]),
    'admissions': ('Admission', [('Name', 'name'), ('Age', 'age'), ('Roll No', 'rollNo'), ('Semester', 'semester')], [('name', 'text'), ('age', 'number'), ('rollNo', 'number'), ('semester', 'number')]),
    'attendances': ('Attendance', [('Roll', 'roll'), ('Name', 'name'), ('Year', 'year'), ('Attendance %', 'attendance')], [('roll', 'number'), ('name', 'text'), ('year', 'number'), ('attendance', 'number')]),
    'examinations': ('Examination', [('Name', 'name'), ('Roll', 'roll'), ('Subjects', 'subjects'), ('Marks', 'marks'), ('Exam Name', 'examName')], [('name', 'text'), ('roll', 'number'), ('subjects', 'text'), ('marks', 'text'), ('examName', 'text')]),
    'reviews': ('Review', [('Name', 'name'), ('Year', 'year'), ('Review', 'reviewString')], [('name', 'text'), ('year', 'number'), ('reviewString', 'text')]),
    'syllabuses': ('Syllabus', [('Subjects', 'subjects')], [('subjects', 'text')]),
    'timetables': ('Timetable', [('Course Name', 'courseName'), ('Course ID', 'courseId'), ('Faculty', 'facultyName'), ('Day', 'day')], [('courseName', 'text'), ('courseId', 'number'), ('facultyName', 'text'), ('day', 'text')])
}

for sec_id, (title, headers, fields) in sections_config.items():
    html_template += generate_section(sec_id, title, headers, fields)

html_template += '''
        </div>
    </main>
'''

for sec_id, (title, headers, fields) in sections_config.items():
    html_template += f'''
    <!-- {title} Modal -->
    <div id="{sec_id}-modal" class="modal fixed inset-0 z-50 bg-gray-900 bg-opacity-50 items-center justify-center backdrop-blur-sm transition-opacity">
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden transform transition-all">
            <div class="bg-gray-50 px-6 py-4 border-b border-gray-100 flex justify-between items-center">
                <h3 class="text-lg font-bold text-gray-800">Add {title}</h3>
                <button onclick="closeModal('{sec_id}-modal')" class="text-gray-400 hover:text-gray-600"><i class="fas fa-times text-xl"></i></button>
            </div>
            <form id="{sec_id}-form" onsubmit="submitForm(event, '{sec_id}')" class="p-6 space-y-4">
                <div id="{sec_id}-form-error" class="hidden text-red-500 text-sm font-bold"></div>
'''
    for field_id, field_type in fields:
        html_template += f'''
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">{field_id}</label>
                    <input type="{field_type}" id="{sec_id}-{field_id}" required class="w-full px-4 py-2 border border-gray-300 rounded-lg outline-none">
                </div>
'''
    html_template += f'''
                <div class="pt-4 flex justify-end space-x-3">
                    <button type="button" onclick="closeModal('{sec_id}-modal')" class="px-4 py-2 text-gray-600 bg-gray-100 rounded-lg">Cancel</button>
                    <button type="submit" class="px-4 py-2 text-white bg-brand-secondary rounded-lg">Save</button>
                </div>
            </form>
        </div>
    </div>
'''

js_code = '''
    <script>
        const API_BASE = window.location.protocol === 'file:' ? 'http://localhost:8080' : '';
        let state = {};
        
        const config = {
'''
for sec_id, (_, headers, fields) in sections_config.items():
    fields_list = [f for f, _ in fields]
    js_code += f"            '{sec_id}': {{ endpoint: '/api/{sec_id if sec_id != 'faculty' else 'faculties'}', fields: {fields_list}, headers: {[h for _, h in headers]} }},\n"

js_code += '''
        };

        function navigate(viewId) {
            document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('bg-brand-primary'));
            document.getElementById('nav-' + viewId).classList.add('bg-brand-primary');
            document.querySelectorAll('.section-view').forEach(s => s.classList.remove('active'));
            document.getElementById(viewId).classList.add('active');
        }

        function openModal(id) { document.getElementById(id).classList.add('active'); }
        function closeModal(id) { 
            document.getElementById(id).classList.remove('active'); 
        }

        async function refreshData() {
            try {
                for (let key in config) {
                    const res = await fetch(`${API_BASE}${config[key].endpoint}`);
                    if (res.ok) {
                        state[key] = await res.json();
                        renderTable(key);
                    }
                }
                document.getElementById('db-status').innerText = 'Online';
                document.getElementById('connection-status').innerText = 'Connected';
            } catch (e) {
                document.getElementById('error-banner').classList.remove('hidden');
                document.getElementById('db-status').innerText = 'Offline';
            }
        }
        
        async function runOp(op) {
            const val = prompt(`Enter value for ${op}:`);
            if(!val) return;
            try {
                const res = await fetch(`${API_BASE}/api/colleges/operation`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ operation: op, value: val })
                });
                const txt = await res.text();
                alert(txt);
            } catch (err) { alert(err.message); }
        }

        function renderTable(key) {
            const tbody = document.getElementById(key + '-table-body');
            tbody.innerHTML = '';
            const items = state[key] || [];
            items.forEach(item => {
                let row = '<tr class="hover:bg-gray-50">';
                config[key].headers.forEach(h => {
                    let val = item[h];
                    if (Array.isArray(val)) val = val.join(', ');
                    row += `<td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${val}</td>`;
                });
                row += '</tr>';
                tbody.insertAdjacentHTML('beforeend', row);
            });
        }

        async function submitForm(e, key) {
            e.preventDefault();
            const payload = {};
            config[key].fields.forEach(f => {
                const el = document.getElementById(`${key}-${f}`);
                if (el.type === 'number') payload[f] = parseFloat(el.value);
                else if (key === 'examinations' && (f === 'subjects' || f === 'marks')) {
                    payload[f] = el.value.split(',').map(s => s.trim());
                    if (f === 'marks') payload[f] = payload[f].map(n => parseInt(n));
                }
                else if (key === 'syllabuses' && f === 'subjects') {
                    payload[f] = el.value.split(',').map(s => s.trim());
                }
                else payload[f] = el.value;
            });

            try {
                const res = await fetch(`${API_BASE}${config[key].endpoint}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                if (res.ok) {
                    const saved = await res.json();
                    if (!state[key]) state[key] = [];
                    state[key].push(saved);
                    renderTable(key);
                    closeModal(`${key}-modal`);
                    e.target.reset();
                }
            } catch (err) { alert(err.message); }
        }

        document.addEventListener('DOMContentLoaded', refreshData);
    </script>
</body>
</html>
'''

html_template += js_code

with open('index.html', 'w', encoding='utf-8') as f:
    f.write(html_template)
