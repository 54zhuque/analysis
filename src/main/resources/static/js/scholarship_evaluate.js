(function () {
    // 页面起始：设置四个等级对应的Vue对象
    window.studentTable_W0 = new Vue({
        el: '#excellent_students_W0',
        data: {studentList: []},
        updated: render_form
    });
    window.studentTable_W1 = new Vue({
        el: '#excellent_students_W1',
        data: {studentList: []},
        updated: render_form
    });
    window.studentTable_W2 = new Vue({
        el: '#excellent_students_W2',
        data: {studentList: []},
        updated: render_form
    });
    window.studentTable_W3 = new Vue({
        el: '#excellent_students_W3',
        data: {studentList: []},
        updated: render_form
    });
    window.studentTable_result = new Vue({
        el: '#excellent_students_result',
        data: {studentList: []}
    });
})();

// layui初始化，加载对应的组建
layui.use(['jquery', "layer", "element"], function () {
    window.$ = layui.jquery;
    var element = layui.element;
    // 添加便签切换监听事件
    element.on("tab(scholarship_tab)", function (data) {
        if (data['index'] <= 3) {
            loadStudentGradeTable('W' + data['index']);
        } else {
            loadScholarshipResultTable();
        }
    });

    // 添加提交按钮的监听事件
    $("button.scholarship_submit_btn").on("click", function () {
        var table_level = $(this).attr("table_level");
        var table = $("#excellent_students_" + table_level);
        var box_list = $(table).find('input[type="checkbox"]');
        for (var i = 0; i < box_list.length; i++) {
            var stuNo = $(box_list[i]).attr('stuNo');
            var result = $(box_list[i]).is(':checked') ? table_level : plusLevel(table_level);
            $.post('/bua/scholarship/evaluating/' + stuNo + '/result/' + result);
        }
        layer.msg('提交成功');
    });

    // 最终提交
    $("#upload_evaluations_scholarship").on("click", function () {
        $.get('/bua/scholarship/evaluated', function () {
            layer.msg('评选结果上传成功');
        });
    });
    // 初始化特等奖的评选页面
    loadStudentGradeTable('W0');
});

/**
 * 加载奖学金评选页面
 * 根据不同的等级，加载对应的数据
 * @param level
 */
function loadStudentGradeTable(level) {
    if (!level) {
        level = 'W0';
    }
    var url = '/bua/scholarship/evaluating/results/' + level;
    $.get(url, null, function (data) {
        var code = data['code'];
        if (parseInt(code) === 200) {
            var showData = data['data'];
            if (showData && showData.length > 0) {
                if (window['studentTable_' + level]) {
                    var table_vue_obj = window['studentTable_' + level];
                    table_vue_obj.$data.studentList = convertShowData(showData);
                }
            }
        }
    });
}

/**
 * 加载奖学金评选结果页面
 */
function loadScholarshipResultTable() {
    var grade = $("#grade").val();
    var subject = $("#subject").val();
    var url = '/bua/analysis/evaluations/' + grade + '/' + subject;
    $.get(url, null, function (data) {
        var code = data['code'];
        if (parseInt(code) === 200) {
            var showData = data['data'];
            if (showData && showData.length > 0) {
                window['studentTable_result'].$data.studentList = convertShowData(showData);
            }
        }
    });
}

/**
 * 数据转换 分数设置为等级，英语成绩做处理
 * @param data_list
 * @returns {*}
 */
function convertShowData(data_list) {
    for (var i = 0; i < data_list.length; i++) {
        var data = data_list[i];
        data['physicalScore'] = getScoreAndLevel(data['physicalScore']);
        data['moralScore'] = getScoreAndLevel(data['moralScore']);
        data['evaluationResult'] = getEvaluationResultText(data['evaluationResult']);
        if (data['englishScore'] === '0.0') {
            data['englishScore'] = '无要求';
        }
    }
    return data_list;
}

/**
 * 将分数转换为等级
 * @param score
 * @returns {string}
 */
function getScoreAndLevel(score) {
    score = parseInt(score);
    var result = score + "(";
    if (score >= 85) {
        result += "优秀";
    } else if (score >= 75) {
        result += "良好";
    } else if (score >= 60) {
        result += "合格";
    } else {
        result += "不合格";
    }
    result += ")";
    return result;
}

/**
 * 计算等价的下一个等级
 * @param level
 * @returns {string}
 */
function plusLevel(level) {
    if (level === 'W0') {
        return 'W1';
    }
    if (level === 'W1') {
        return 'W2';
    }
    if (level === 'W2') {
        return 'W3';
    }
    return '';
}

function render_form() {
    layui.use('form', function () {
        layui.form.render();
    })
}

function getEvaluationResultText(level) {
    var result = '';
    switch (level) {
        case('A'):
            result = '三好学生';
            break;
        case('B'):
            result = '优秀班干部';
            break;
        case('W0'):
            result = '特等奖学金';
            break;
        case('W1'):
            result = '一等奖学金';
            break;
        case('W2'):
            result = '二等奖学金';
            break;
        case('W3'):
            result = '三等奖学金';
            break;
        default:
            break;
    }
    return result;
}

function exportTableData() {
    var workbook = XLSX.utils.table_to_book(document.getElementById('scholarship_student_result'));
    var wopts = {bookType: 'xlsx', bookSST: false, type: 'array'};
    var wbout = XLSX.write(workbook, wopts);
    saveAs(new Blob([wbout], {type: "application/octet-stream"}), '奖学金.xlsx');
}