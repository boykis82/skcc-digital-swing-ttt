var main = {
    init: function() {
        var _this = this;
        $('#btn-save').on('click', function() {
            _this.save();
        });
    },

    save: function() {

        var custTypCd = {
            key: 'C01',
            value: '개인'
        };

        var data = {
            custNm: $('#custNm').val(),
            birthDt: $('#birthDt').val(),
            custTypCd: custTypCd
        };

        $.ajax({
            type: 'POST',
            url: '/legacy-customer/swing/api/v1/customers',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('고객 등록 완료!');
            window.location.href = '/';
        }).fail(function(error) {
            alert(JSON.stringify(error));
        });
    }
};

main.init();