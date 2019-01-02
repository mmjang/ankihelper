document.querySelectorAll("p img").forEach(function(p){
        if(!p.src){
            p.src=p.getAttribute('data-original')
        }
        p.style.width="95%";
        p.style.height= "auto";
    }
)