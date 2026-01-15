import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { FormateurService } from '../../core/formateur.service';
import { Cours } from '../../core/models';

interface StudentNote {
  id: number;
  prenom: string;
  nom: string;
  ds: number | null;
  examen: number | null;
  moyenne: number | null;
}

interface GroupStudentsData {
  etudiants: StudentNote[];
  moyenneGenerale: number | null;
  tauxReussite: number;
}

@Component({
  selector: 'app-group-students',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './group-students.component.html',
  styleUrl: './group-students.component.scss'
})
export class GroupStudentsComponent implements OnInit {
  groupeId: number;
  coursForm: FormGroup;
  data: GroupStudentsData | null = null;
  loading = false;
  error = '';
  myCourses: Cours[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private formateur: FormateurService
  ) {
    this.groupeId = +this.route.snapshot.params['groupeId'];
    this.coursForm = this.fb.group({
      coursId: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadMyCourses();
  }

  loadMyCourses() {
    this.formateur.getCourses().subscribe({
      next: (courses) => {
        this.myCourses = courses;
        if (this.myCourses.length > 0) {
          this.coursForm.patchValue({ coursId: this.myCourses[0].id });
          this.loadStudents();
        }
      },
      error: (err) => {
        console.error('Failed to load courses', err);
        this.error = 'Impossible de charger les cours.';
      }
    });
  }

  onCoursChange() {
    this.loadStudents();
  }

  loadStudents() {
    if (!this.coursForm.valid) return;
    const coursId = this.coursForm.value.coursId;
    this.loading = true;
    this.error = '';
    this.formateur.getGroupStudentsNotes(this.groupeId, coursId).subscribe({
      next: (data) => {
        this.data = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load students', err);
        this.error = 'Impossible de charger les étudiants.';
        this.loading = false;
      }
    });
  }

  exportPdf() {
    if (!this.coursForm.valid) return;
    const coursId = this.coursForm.value.coursId;
    this.formateur.exportGroupStudentsPdf(this.groupeId, coursId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `liste_etudiants_groupe_${this.groupeId}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('Failed to export PDF', err);
        this.error = 'Erreur lors de l\'export PDF.';
      }
    });
  }

  backToDashboard() {
    this.router.navigate(['/dashboard']);
  }
}