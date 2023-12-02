import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SelectMenu } from '../../model/select-menu';
import { FormsModule, NgModel } from '@angular/forms';

@Component({
  selector: 'app-select-menu',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [NgModel],
  templateUrl: './select-menu.component.html',
  styleUrls: ['./select-menu.component.css'],
})
export class SelectMenuComponent {
  @Input() values: SelectMenu[] = [];
  @Input() placeHolder = '';
  @Output() selectedValueEvent = new EventEmitter<any>();

  onValueSelect(event: any) {
    this.selectedValueEvent.emit(event);
  }
}
