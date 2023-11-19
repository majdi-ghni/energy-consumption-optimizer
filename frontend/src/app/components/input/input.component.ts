import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-input',
  standalone: true,
  templateUrl: './input.component.html',
  styleUrl: './input.component.css'
})
export class InputComponent {
  @Input() type: string = '';
  @Input() placeholder: string = '';

  @Output() inputChange = new EventEmitter<string>();
  inputValueChange(input: string): void {
    this.inputChange.emit(input)
  }
}
