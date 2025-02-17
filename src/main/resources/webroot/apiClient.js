const apiClient = (() => {

    const urlSpark = '/app/';
    const urlSpring = '/spring/';

    const getNotes = async () => {
        const response = await fetch(urlSpark + 'note');
        return response.json();
    }

    const addNote = async (title, group, content ) => {
        
        const promise = await fetch(`${urlSpring}note?title=${encodeURIComponent(title)}&group=${encodeURIComponent(group)}&content=${encodeURIComponent(content)}`, {
            method: 'POST',
        });
        return promise;
    }

    const greeting = async (name) => {
        if(name){
            const promise = await fetch(`${urlSpring}hello?name=${encodeURIComponent(name)}`);
            return promise.json();
        }
        else{
            const promise = await fetch(`${urlSpring}hello`);
            return promise.json();
        }

    }

    return {
        getNotes,
        addNote,
        greeting
    }
})();