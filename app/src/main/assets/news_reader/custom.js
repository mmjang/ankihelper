document.querySelectorAll(".inline-photo img").forEach(p=>
    p.src=p.getAttribute('data-low-res-src')
)