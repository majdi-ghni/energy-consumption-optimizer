import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-select-menu',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './select-menu.component.html',
  styleUrls: ['./select-menu.component.css'],
})
export class SelectMenuComponent {
  @Input() values: string[] = [];
  @Input() placeHolder = '';
  @Output() selectedValueEvent = new EventEmitter<string>();

  onValueSelect(value: any) {
    this.selectedValueEvent.emit(value.value);
  }
}
