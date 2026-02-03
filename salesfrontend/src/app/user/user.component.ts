import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { Chart, registerables } from 'chart.js';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit {

  selectedMenu = 'profile';

  leads: any[] = [];
  projects: any[] = [];

  mail = '';

  selectedTaskDate: string = '';

  
  tasks: any[] = [];


  filterStatus = '';
startDate = '';
endDate = '';


dailyReport: any = {};
reportDate = new Date().toISOString().split('T')[0];
  displayName: string | undefined;

loadDailyReport() {
  this.http.get<any>(
    `${this.baseUrl}/user/report/daily`,
    {
      params: {
        mail: this.mail,
        date: this.reportDate
      }
    }
  ).subscribe(res => {
    if (res.success) {
      this.dailyReport = res.data;
    } else {
      this.dailyReport = {};
    }
  });
}


projectStatus = '';
get filteredProjects() {
  return this.projects.filter(p =>
    !this.projectStatus || p.status === this.projectStatus
  );
}



applyMyLeadFilters() {
  const params: any = {
    mail: this.mail
  };

  if (this.filterStatus) params.status = this.filterStatus;
  if (this.startDate) params.start = this.startDate;
  if (this.endDate) params.end = this.endDate;

  this.http.get<any>(
    `${this.baseUrl}/leads/filter`,
    { params }
  ).subscribe(res => {
    if (res.success) {
      // this.leads = res.data;
      this.leads = res.data.sort((a: any, b: any) => b.id - a.id);
    }
  });
}

resetMyLeadFilters() {
  this.filterStatus = '';
  this.startDate = '';
  this.endDate = '';
  this.loadLeads();
}


  loadTodayTasks() {
  this.http.get<any>(
    `${this.baseUrl}/tasks/today?mail=${this.mail}`
  ).subscribe(res => {
    if (res.success) {
      this.tasks = res.data;
    } else {
      this.tasks = [];
    }
  });
}


markTaskDone(taskId: number) {
  this.http.put<any>(
    `${this.baseUrl}/tasks/mark-done/${taskId}`,
    {}
  ).subscribe(res => {
    if (res.success) {
      this.loadAllTasks(); // or loadAllTasks if you switch later
    }
  });
}


loadUserTasksByDate() {
  if (!this.selectedTaskDate) {
    this.tasks = [];
    return;
  }

  this.http.get<any>(
    `${this.baseUrl}/tasks/by-date?mail=${this.mail}&date=${this.selectedTaskDate}`
  ).subscribe(res => {
    this.tasks = res.success ? res.data : [];
  });
}

resetUserTaskFilter() {
  this.selectedTaskDate = '';
  this.loadAllTasks();
}



  /* ================= DASHBOARD DATA ================= */
  dashboardSummary: any = {};
  leadFunnel: any = {};
  leadFunnelRows: { status: string; count: number }[] = [];

  /* ================= CHARTS ================= */
  leadsChart: Chart<'doughnut', number[], string> | null = null;
  projectsChart: Chart<'doughnut', number[], string> | null = null;

  /* ================= MODALS ================= */
  showModal = false;
  showDescModal = false;
  isEdit = false;
  selectedDescription = '';

  // statuses = ['Interested', 'Not Interested', 'Follow Up', 'Closed'];
  statuses = [
  'Interested',
  'Not Interested',
  'Follow Up',
  'Quotation',
  'Call',
  'Closed',
];

  leadForm: any = {};

  private baseUrl = 'http://localhost:8080';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    Chart.register(...registerables);
  }

  ngOnInit() {
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    const role = localStorage.getItem('role');
    this.mail = localStorage.getItem('mail') || '';

    if (isLoggedIn !== 'true' || role !== 'USER' || !this.mail) {
      this.router.navigate(['/login']);
      return;
    }

    if (this.mail) {
    this.displayName = this.mail.split('@')[0];
    }

    this.loadDashboard();
    this.loadLeads();
  }

  selectMenu(menu: string) {
  this.selectedMenu = menu;

  if (menu === 'profile') {
    this.loadDashboard();
  }

  if (menu === 'leads') {
    this.loadLeads();
  }

  if(menu=='projects'){
    this.loadProjects();
  }

 if (menu === 'tasks') {
  this.selectedTaskDate = '';
  this.loadAllTasks();
}

  if (menu === 'reports') {
    this.loadDailyReport();
  }



}


  /* ================= DASHBOARD ================= */

  loadDashboard() {
    this.http.get<any>(
      `${this.baseUrl}/userdashboard/summary?mail=${this.mail}`
    ).subscribe(res => {
      if (res.success) {
        this.dashboardSummary = res.data;
        setTimeout(() => this.renderCharts(), 0);
      }
    });

    this.http.get<any>(
      `${this.baseUrl}/userdashboard/lead-summary?mail=${this.mail}`
    ).subscribe(res => {
      if (res.success) {
        this.leadFunnel = res.data;
        this.leadFunnelRows = Object.keys(this.leadFunnel).map(key => ({
          status: key,
          count: this.leadFunnel[key]
        }));
      }
    });
  }

  renderCharts() {
  // ✅ only render if profile tab is active
  if (this.selectedMenu !== 'profile') return;

  const leadsCanvas = document.getElementById('userLeadsDonut') as HTMLCanvasElement;
  const projectsCanvas = document.getElementById('userProjectsDonut') as HTMLCanvasElement;

  // ✅ canvas not ready yet
  if (!leadsCanvas || !projectsCanvas) return;

  if (this.leadsChart) this.leadsChart.destroy();
  if (this.projectsChart) this.projectsChart.destroy();

  this.leadsChart = new Chart(leadsCanvas, {
    type: 'doughnut',
    data: {
      labels: ['Assigned', 'Unassigned'],
      datasets: [{
        data: [
          this.dashboardSummary.assignedLeads,
          this.dashboardSummary.totalLeads - this.dashboardSummary.assignedLeads
        ],
        backgroundColor: ['#2563eb', '#cbd5e1']
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { position: 'bottom' } }
    }
  });

  this.projectsChart = new Chart(projectsCanvas, {
    type: 'doughnut',
    data: {
      labels: ['Active', 'Inactive'],
      datasets: [{
        data: [
          this.dashboardSummary.activeProjects,
          this.dashboardSummary.totalProjects - this.dashboardSummary.activeProjects
        ],
        backgroundColor: ['#16a34a', '#e5e7eb']
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { position: 'bottom' } }
    }
  });
}


  /* ================= LEADS ================= */

  loadLeads() {
    this.http.get<any>(`${this.baseUrl}/getleads/${this.mail}`)
      .subscribe(res => {
        if (res.success) {
          this.leads = res.data.sort((a: any, b: any) => b.id - a.id);
        }
      });
  }

  openAddModal() {
  this.isEdit = false;
  this.leadForm = {
    clientName: '',
    companyName: '',
    mobileno: '',
    projectType: '',
    budgetRange: '',
    timeline: '',
    reference: '',
    status: 'Interested',
    description: '',
    mail: this.mail,

    // 🔥 task fields
    taskType: null,
    taskDate: null,
    taskNote: ''
  };
  this.showModal = true;
}


  openEditModal(lead: any) {
  this.isEdit = true;
  this.leadForm = {
    ...lead,
    taskType: null,
    taskDate: null,
    taskNote: ''
  };
  this.showModal = true;
}


  saveLead() {
    if (this.isTaskRequired() && !this.leadForm.taskDate) {
  alert('Please select a task date');
  return;
}
if (!this.isTaskRequired()) {
  delete this.leadForm.taskType;
  delete this.leadForm.taskDate;
  delete this.leadForm.taskNote;
}

  if (this.isEdit) {
    this.http.put<any>(
      `${this.baseUrl}/updatelead`,
      this.leadForm
    ).subscribe(res => {
      if (res.success) {
        this.showModal = false;
        this.loadLeads();
        this.loadDashboard();
      }
    });
  } else {
    this.http.post<any>(
      `${this.baseUrl}/addlead`,
      this.leadForm
    ).subscribe(res => {
      if (res.success) {
        this.showModal = false;
        this.loadLeads();
        this.loadDashboard();
      }
    });
  }
}


  openDescription(desc: string) {
    this.selectedDescription = desc || 'No description provided.';
    this.showDescModal = true;
  }

  closeDescription() {
    this.showDescModal = false;
  }

  closeModal() {
    this.showModal = false;
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }


// load projects for the user per gmail
  loadProjects() {
  this.http.get<any>(
    `${this.baseUrl}/getprojects/by-mail/${this.mail}`
  ).subscribe(res => {
    if (res.success) {
      this.projects = res.data;
    } else {
      this.projects = [];
    }
  });
}

isTaskRequired(): boolean {
  return (
    this.leadForm.status === 'Follow Up' ||
    this.leadForm.status === 'Quotation' ||
    this.leadForm.status === 'Call'
  );
}

onStatusChange() {
  if (this.isTaskRequired()) {
    this.leadForm.taskType =
      this.leadForm.status === 'Follow Up' ? 'FOLLOW_UP' :
      this.leadForm.status === 'Quotation' ? 'QUOTATION' :
      this.leadForm.status === 'Call' ? 'CALL' :
      null;
  } else {
    this.leadForm.taskType = null;
    this.leadForm.taskDate = null;
    this.leadForm.taskNote = '';
  }
}

loadAllTasks() {
  this.http.get<any>(
    `${this.baseUrl}/tasks/by-mail?mail=${this.mail}`
  ).subscribe(res => {
    this.tasks = res.success ? res.data : [];
  });
}


isSidebarOpen = false;

toggleSidebar() {
  this.isSidebarOpen = !this.isSidebarOpen;
}

closeSidebar() {
  this.isSidebarOpen = false;
}



}
