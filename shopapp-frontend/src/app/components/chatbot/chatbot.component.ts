// File: chatbot.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-chatbot',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './chatbot.component.html',
    styleUrl: './chatbot.component.scss'
})
export class ChatbotComponent {
    isOpen: boolean = false;
    userMessage: string = '';
    currentView: 'menu' | 'bot' | 'human' = 'menu';

    botMessages: Array<{ text: string, sender: 'user' | 'bot' }> = [
        { text: 'Xin chào! Tôi là trợ lý AI của NochTech.', sender: 'bot' },
        { text: 'Tôi có thể giúp gì cho bạn?', sender: 'bot' }
    ];

    humanMessages: Array<{ text: string, sender: 'user' | 'bot' }> = [
        { text: 'Chào bạn, nhân viên tư vấn sẽ phản hồi trong giây lát...', sender: 'bot' }
    ];

    toggleChat() {
        this.isOpen = !this.isOpen;
        if (this.isOpen) {
            this.currentView = 'menu';
        }
    }

    selectMode(mode: 'bot' | 'human') {
        this.currentView = mode;
    }

    goBack() {
        this.currentView = 'menu';
    }

    get currentMessages() {
        if (this.currentView === 'bot') return this.botMessages;
        if (this.currentView === 'human') return this.humanMessages;
        return [];
    }

    sendMessage() {
        if (!this.userMessage.trim()) return;
        const currentList = this.currentView === 'bot' ? this.botMessages : this.humanMessages;
        currentList.push({ text: this.userMessage, sender: 'user' });
        const msgContent = this.userMessage;
        this.userMessage = '';

        setTimeout(() => {
            if (this.currentView === 'bot') {
                this.botMessages.push({ text: 'Bot: Đang tìm kiếm "' + msgContent + '"...', sender: 'bot' });
            } else if (this.currentView === 'human') {
                this.humanMessages.push({ text: 'Nhân viên: Đã nhận yêu cầu về "' + msgContent + '".', sender: 'bot' });
            }
        }, 1000);
    }
}